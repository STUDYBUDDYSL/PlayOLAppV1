package com.taloslogy.playolapp.repository

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class LoginRepository {

    companion object {
        private const val ssoUrl = "http://greentel.taloslogy.net:8080/sso?authToken="
    }

    fun ssoLoginRequest(authToken: String, onResult: (res: Boolean, msg: String) -> Unit) {

        val url = "$ssoUrl$authToken"

        val httpSSO = Fuel.post(url).response { _, _, result ->
            when(result){
                is Result.Failure -> {
                    val err = result.getException().message
                    var msg = ""
                    if(!err.isNullOrBlank()){
                        msg = if(err.contains("Unable to resolve host")){
                            "Device doesn't have an internet connection!"
                        } else {
                            "Server error. Please try again!"
                        }
                    }

                    onResult(false, msg)
                }
                is Result.Success -> {
                    onResult(true, "")
                }
            }
        }
        httpSSO.join()
    }

}