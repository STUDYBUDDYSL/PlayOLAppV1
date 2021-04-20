package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.UserDetailRepository
import com.taloslogy.playolapp.utils.StringUtils
import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCException
import de.timroes.axmlrpc.XMLRPCServerException

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class SubscriptionViewModel: ViewModel() {

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
            Log.d("TEST_RESPONSE", result.toString())
            Log.d("TEST_RESPONSE", "${result!!::class.simpleName}")
            activation.postValue(LoginResult(LoginRes.LoginSuccess))
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

}