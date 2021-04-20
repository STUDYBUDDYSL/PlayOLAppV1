package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.LoginRepository
import com.taloslogy.playolapp.utils.storage.PrefHelper

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserViewModel(private val prefs: PrefHelper) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var token: String? = null

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val loginCycle = MutableLiveData<LoginResult>(LoginResult(LoginRes.LoginWaiting))

    private val loginRepo: LoginRepository = LoginRepository()

    fun checkLogin() : LiveData<Boolean> {
        checkForKeys()
        return _userLoggedIn
    }

    private fun checkForKeys() {
//        prefs.aesKeyPref.delete()
//        prefs.aesIvPref.delete()
//        prefs.partKeyPref.delete()
//        prefs.userPref.delete()
//        prefs.key1Pref.delete()
//        prefs.key2Pref.delete()
//        prefs.iv1Pref.delete()
//        prefs.iv2Pref.delete()
        _userLoggedIn.value = prefs.userPref.get()
    }

    fun googleSignIn(fName: String, lName: String, email: String, token: String) {
        this.name = "$fName $lName"
        this.email = email
        this.token = token

        loginCycle.postValue(LoginResult(LoginRes.LoginLoading))
        try{
            Log.d("TEST_LOG", token)
            // Complete SSO login
            loginRepo.ssoLoginRequest(token) { result, msg ->
                if(result){
                    Log.d("TEST_LOG", "Navigate...")
                    loginCycle.postValue(LoginResult(LoginRes.LoginSuccess))
                }
                else {
                    loginCycle.postValue(LoginResult(LoginRes.LoginError, msg))
                }
            }
        }
        catch(e: Exception){
            loginCycle.postValue(LoginResult(LoginRes.LoginError))
        }
    }

}