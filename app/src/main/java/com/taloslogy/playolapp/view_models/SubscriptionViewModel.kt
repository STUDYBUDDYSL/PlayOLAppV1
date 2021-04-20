package com.taloslogy.playolapp.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class SubscriptionViewModel: ViewModel() {

    val activation = MutableLiveData<LoginResult>(LoginResult(LoginRes.LoginWaiting))

    val setQR = MutableLiveData<String>("")
    val qrCode = MutableLiveData<String>("")

}