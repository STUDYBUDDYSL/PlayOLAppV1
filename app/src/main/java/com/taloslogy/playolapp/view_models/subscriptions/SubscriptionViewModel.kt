package com.taloslogy.playolapp.view_models.subscriptions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SubscriptionViewModel: ViewModel() {

    private val _qrCode = MutableLiveData<String>()

    val qrCode : LiveData<String?> get() = _qrCode

    fun setQRCode(code: String?){
        Log.d("TEST_LOG", code ?: "null")
        _qrCode.value = code
    }

}