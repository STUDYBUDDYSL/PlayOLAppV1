package com.taloslogy.playolapp.models

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
enum class LoginRes {
    LoginWaiting,
    LoginLoading,
    LoginSuccess,
    LoginError
}

class LoginResult(val type: LoginRes, val message: String = "Couldn't complete login. Please try again!")