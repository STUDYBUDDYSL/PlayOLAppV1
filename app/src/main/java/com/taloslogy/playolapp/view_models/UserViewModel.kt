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

    fun loginComplete() {

        // TODO(Get the activation codes from a network call)

        // Set the keys retrieved from api
        prefs.aesKeyPref.set("017035012004015000026013125142159157167068063068035010145002029020158170135154155119155126")
        prefs.aesIvPref.set("017035012004015000026013125142159157167068063068035010145002029014045167149060")
        prefs.partKeyPref.set("08d40f121863624257654e6473674d39555a747430695765476466733308d40f120c782b61584a7e4b415d2a7d66")
        prefs.key1Pref.set("017035012004015000026013125142159157167068063068035010145002029020168165160150147173132106")
        prefs.key2Pref.set("017035012004015000026013125142159157167068063068035010145002029020108123131127112165166160")
        prefs.iv1Pref.set("017035012004015000026013125142159157167068063068035010145002029014145078137180")
        prefs.iv2Pref.set("017035012004015000026013125142159157167068063068035010145002029014147110150057")

        // Complete login flow and not show a second time
        prefs.userPref.set(true)
    }

    fun googleSignIn(fName: String, lName: String, email: String, token: String) {
        this.name = "$fName $lName"
        this.email = email

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
                    //TODO display error
                    loginCycle.postValue(LoginResult(LoginRes.LoginError, msg))
                }
            }
        }
        catch(e: Exception){
            Log.e("TEST_LOG_E", e.toString())
            loginCycle.postValue(LoginResult(LoginRes.LoginError))
        }
    }

}