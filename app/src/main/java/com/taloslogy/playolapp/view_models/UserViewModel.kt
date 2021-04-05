package com.taloslogy.playolapp.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {

    var test: Boolean = false
    var userId: Int? = null

    private val _userLoggedIn = MutableLiveData<Boolean>()

    fun checkLogin() : LiveData<Boolean> {
        checkForKeys()
        return _userLoggedIn
    }

    private fun checkForKeys() {
        // Do an asynchronous operation to find the keys if existing logged in
        _userLoggedIn.value = test
    }

    fun loginComplete() {
        test = true
        userId = 2
    }

}