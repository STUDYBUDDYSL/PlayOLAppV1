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
        private const val LOG_STATUS = "LoggedIn"
        private const val KEY_PART_1 = "KEY_PART_1"
        private const val KEY_PART_2 = "KEY_PART_2"
        private const val IV_PART_1 = "IV_PART_1"
        private const val IV_PART_2 = "IV_PART_2"
    }

    val userPref: UserKeyPreference = UserKeyPreference(pref, LOG_STATUS)
    val key1Pref: KeyPart1Preference = KeyPart1Preference(pref, KEY_PART_1)
    val key2Pref: KeyPart2Preference = KeyPart2Preference(pref, KEY_PART_2)
    val iv1Pref: IVPart1Preference = IVPart1Preference(pref, IV_PART_1)
    val iv2Pref: IVPart2Preference = IVPart2Preference(pref, IV_PART_2)

}

class UserKeyPreference(preferences: SharedPreferences, key: String) : BooleanPreference(preferences, key)

class KeyPart1Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class KeyPart2Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class IVPart1Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class IVPart2Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)