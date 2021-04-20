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
        private const val AES_KEY = "AES_KEY"
        private const val AES_IV = "AES_IV"
        private const val PART_KEY = "PART_KEY"
        private const val KEY_PART_1 = "KEY_PART_1"
        private const val KEY_PART_2 = "KEY_PART_2"
        private const val IV_PART_1 = "IV_PART_1"
        private const val IV_PART_2 = "IV_PART_2"
    }

    fun clearPrefs(){
        aesKeyPref.delete()
        aesIvPref.delete()
        partKeyPref.delete()
        userPref.delete()
        key1Pref.delete()
        key2Pref.delete()
        iv1Pref.delete()
        iv2Pref.delete()
    }

    val userPref: UserKeyPreference = UserKeyPreference(pref, LOG_STATUS)
    val aesKeyPref: AesKeyPreference = AesKeyPreference(pref, AES_KEY)
    val aesIvPref: AesIvPreference = AesIvPreference(pref, AES_IV)
    val partKeyPref: PartKeyPreference = PartKeyPreference(pref, PART_KEY)
    val key1Pref: KeyPart1Preference = KeyPart1Preference(pref, KEY_PART_1)
    val key2Pref: KeyPart2Preference = KeyPart2Preference(pref, KEY_PART_2)
    val iv1Pref: IVPart1Preference = IVPart1Preference(pref, IV_PART_1)
    val iv2Pref: IVPart2Preference = IVPart2Preference(pref, IV_PART_2)

}

class UserKeyPreference(preferences: SharedPreferences, key: String) : BooleanPreference(preferences, key)

class AesKeyPreference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class AesIvPreference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class PartKeyPreference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class KeyPart1Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class KeyPart2Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class IVPart1Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)

class IVPart2Preference(preferences: SharedPreferences, key: String) : StringKeyStore(preferences, key)