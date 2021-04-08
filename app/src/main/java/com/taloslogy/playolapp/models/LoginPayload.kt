package com.taloslogy.playolapp.models

sealed class LoginPayload {
    object LoginWaiting: LoginPayload()
    object LoginLoading: LoginPayload()
    object LoginSuccess: LoginPayload()
    object LoginError: LoginPayload()
}