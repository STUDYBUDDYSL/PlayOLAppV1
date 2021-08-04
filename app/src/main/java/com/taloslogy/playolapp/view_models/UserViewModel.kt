package com.taloslogy.playolapp.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.utils.storage.PrefHelper

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserViewModel(private val prefs: PrefHelper) : ViewModel() {

    private val _userLoggedIn = MutableLiveData<Boolean>()

    fun checkLogin() : LiveData<Boolean> {
        checkForKeys()
        return _userLoggedIn
    }

    private fun checkForKeys() {
        val res = prefs.userPref.get()
        if(!res) saveKeys()
        _userLoggedIn.value = true
    }

    private fun saveKeys(){

        // Set the keys retrieved from api
        prefs.aesKeyPref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d01409e0aa08709a09b07709b07e"))
        prefs.aesIvPref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d00e02d0a709503c"))
        prefs.partKeyPref.set("08d40f121863624257654e6473674d39555a747430695765476466733308d40f120c782b61584a7e4b415d2a7d66")
        prefs.key1Pref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d0140a80a50a00960930ad08406a"))
        prefs.key2Pref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d01406c07b08307f0700a50a60a0"))
        prefs.iv1Pref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d00e09104e0890b4"))
        prefs.iv2Pref.set(hexStrToStr("01102300c00400f00001a00d07d08e09f09d0a704403f04402300a09100201d00e09306e096039"))

        // Complete login flow and not show a second time
        prefs.userPref.set(true)
    }

    private fun hexStrToStr(str: String): String {
        val arr = arrayListOf<String>()
        for (x in str.indices step 3){
            val s = str.substring(x, x+3)
            val s16 = s.toInt(16).toString().padStart(3, '0')
            arr.add(s16)
        }
        return arr.joinToString("")
    }


}