package com.taloslogy.playolapp.models

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
sealed class LoginPayload {
    object LoginWaiting: LoginPayload()
    object LoginLoading: LoginPayload()
    object LoginSuccess: LoginPayload()
    object LoginError: LoginPayload()
}