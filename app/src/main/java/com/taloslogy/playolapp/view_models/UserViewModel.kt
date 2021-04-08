package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.repository.LoginRepository

class UserViewModel: ViewModel() {

    var test: Boolean = false
    var userId: Int? = null

    var name: String? = null
    var email: String? = null

    private val _userLoggedIn = MutableLiveData<Boolean>()

    private val loginRepo: LoginRepository = LoginRepository()

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

    fun googleSignIn(fName: String, lName: String, email: String, token: String) {
        this.name = "$fName $lName"
        this.email = email

        try{
            Log.d("TEST_LOG", token)
            // Complete SSO login
            loginRepo.makeLoginRequest(token) { result ->
                if(result){
                    Log.d("TEST_LOG", "Navigate...")
                }
                else {
                    //TODO display error
                }
            }
        }
        catch(e: Exception){
            Log.e("TEST_LOG_E", e.toString())
        }
    }

}