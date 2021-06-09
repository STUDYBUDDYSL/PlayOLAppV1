package com.taloslogy.playolapp.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.UserDetailRepository
import com.taloslogy.playolapp.utils.storage.PrefHelper
import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCException
import de.timroes.axmlrpc.XMLRPCServerException

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class SubscriptionViewModel(private val prefs: PrefHelper): ViewModel() {

    private val userRepo: UserDetailRepository = UserDetailRepository()
    val activation = MutableLiveData<LoginResult>(LoginResult(LoginRes.LoginWaiting))

    val setQR = MutableLiveData<String>("")
    val qrCode = MutableLiveData<String>("")

    var token: String? = null

    private val authListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            if (result is Int){
                val params = arrayListOf(1, mapOf(
                    "uid" to result,
                    "activationcode" to qrCode.value
                ))
                userRepo.activateSubscription(detailListener, result, token!!, params)
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "Server error. Please try again!"))
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
        }
    }

    private val detailListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            if(result is HashMap<*, *>){
                saveKeys(result as HashMap<String, String>)
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "The entered activation code is wrong or has been activated already!"))
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
        }
    }

    fun activateSubscription(userName: String, token: String) {
        activation.postValue(LoginResult(LoginRes.LoginLoading))
        this.token = token
        userRepo.authenticate(authListener, userName, token)
    }

    private fun saveKeys(keys: HashMap<String, String>){

        // Set the keys retrieved from api
        prefs.aesKeyPref.set(hexStrToStr(keys.getValue("aes_key")))
        prefs.aesIvPref.set(hexStrToStr(keys.getValue("iv_key")))
        prefs.partKeyPref.set(keys.getValue("part_key"))
        prefs.key1Pref.set(hexStrToStr(keys.getValue("half_key_one")))
        prefs.key2Pref.set(hexStrToStr(keys.getValue("half_key_two")))
        prefs.iv1Pref.set(hexStrToStr(keys.getValue("half_iv_one")))
        prefs.iv2Pref.set(hexStrToStr(keys.getValue("half_iv_two")))

        // Complete login flow and not show a second time
        prefs.userPref.set(true)
        activation.postValue(LoginResult(LoginRes.LoginSuccess))
    }

    private fun hexStrToStr(str: String): String {
        val arr = arrayListOf<String>()
        for (x in str.indices step 3){
            val s = str.substring(x, x+3)
            val s16 = s.toInt(16).toString().padStart(3, '0')
            arr.add(s16)
        }
        return arr.joinToString("")
    }

}