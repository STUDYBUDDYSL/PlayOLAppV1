package com.taloslogy.playolapp.repository

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import java.lang.Exception

class LoginRepository {

    companion object {
        private const val ssoUrl = "http://jack-frost.taloslogy.net:5001/User?authToken="
    }

    fun makeLoginRequest(authToken: String, onResult: (res: Boolean) -> Unit) {

        val url = "$ssoUrl$authToken"

        val httpSSO = Fuel.post(url).response { _, _, result ->
            when(result){
                is Result.Failure -> {
                    result.getException().message?.let { Log.e("TEST_LOG", it) }
                    onResult(false)
                }
                is Result.Success -> {
                    Log.d("TEST_LOG", "Logged in..")
                    onResult(true)
                }
            }
        }
        httpSSO.join()
    }

}