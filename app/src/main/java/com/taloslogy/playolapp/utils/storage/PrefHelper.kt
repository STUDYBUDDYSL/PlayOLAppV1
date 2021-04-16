package com.taloslogy.playolapp.utils.storage

import android.content.Context
import android.content.SharedPreferences

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class PrefHelper private constructor(pref: SharedPreferences) {

    companion object {

        @Volatile private var INSTANCE: PrefHelper? = null

        fun getInstance(app: Context): PrefHelper = INSTANCE ?: synchronized(this){
            INSTANCE ?: setSharedPrefInstance(app).also { INSTANCE = it }
        }

        private fun setSharedPrefInstance(app: Context) = PrefHelper(app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE))

        private const val PREF_NAME = "PlayOLPref"
        private const val USER_ID = "OdooId"
        private const val AES_KEY_1 = "AES_1"
    }

    val userPref: UserKeyPreference = UserKeyPreference(pref, USER_ID, null)

}

class UserKeyPreference(preferences: SharedPreferences, key: String, defaultValue: String?)
    : StringKeyStore(preferences, key, defaultValue)