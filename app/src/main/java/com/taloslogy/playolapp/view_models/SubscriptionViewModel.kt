package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.UserDetailRepository
import com.taloslogy.playolapp.utils.StringUtils
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
            Log.e("TEST_SERVER_ERROR", error.toString())
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
            Log.e("TEST_ERROR", error.toString())
        }
    }

    private val detailListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            Log.d("TEST_RESPONSE", "${result!!::class.simpleName}")
            if(result is HashMap<*, *>){
                saveKeys(result as HashMap<String, String>)
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "The entered activation code is wrong or has been activated already!"))
            Log.e("TEST_SERVER_ERROR", error.toString())
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            activation.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
            Log.e("TEST_ERROR", error.toString())
        }
    }

    fun activateSubscription(userName: String, token: String) {
        activation.postValue(LoginResult(LoginRes.LoginLoading))
        this.token = token
        userRepo.authenticate(authListener, userName, token)
    }

    private fun saveKeys(keys: HashMap<String, String>){
        Log.d("TEST_RESPONSE", keys.keys.toString())

        // Set the keys retrieved from api
        prefs.aesKeyPref.set("017035012004015000026013125142159157167068063068035010145002029020158170135154155119155126")
        prefs.aesIvPref.set("017035012004015000026013125142159157167068063068035010145002029014045167149060")
        prefs.partKeyPref.set("08d40f121863624257654e6473674d39555a747430695765476466733308d40f120c782b61584a7e4b415d2a7d66")
        prefs.key1Pref.set("017035012004015000026013125142159157167068063068035010145002029020168165160150147173132106")
        prefs.key2Pref.set("017035012004015000026013125142159157167068063068035010145002029020108123131127112165166160")
        prefs.iv1Pref.set("017035012004015000026013125142159157167068063068035010145002029014145078137180")
        prefs.iv2Pref.set("017035012004015000026013125142159157167068063068035010145002029014147110150057")

        // Complete login flow and not show a second time
        prefs.userPref.set(true)
        activation.postValue(LoginResult(LoginRes.LoginSuccess))
    }

}