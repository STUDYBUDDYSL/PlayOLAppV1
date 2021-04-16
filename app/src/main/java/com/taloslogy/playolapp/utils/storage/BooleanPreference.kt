package com.taloslogy.playolapp.utils.storage

import android.content.SharedPreferences

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class BooleanPreference(private val preference: SharedPreferences, private val key: String) {

    fun get() : Boolean {
        return preference.getBoolean(key, false)
    }

    fun isSet() : Boolean {
        return preference.contains(key)
    }

    fun set(value: Boolean) {
        preference.edit().putBoolean(key, value).apply()
    }

    fun delete() {
        preference.edit().remove(key).apply()
    }

}