package com.taloslogy.playolapp.utils.storage

import android.content.SharedPreferences

class StringPreference(private val preference: SharedPreferences, private val key: String) {

    fun get() : String {
        return preference.getString(key, "")!!
    }

    fun isSet() : Boolean {
        return preference.contains(key)
    }

    fun set(value: String) {
        preference.edit().putString(key, value).apply()
    }

    fun delete() {
        preference.edit().remove(key).apply()
    }

}