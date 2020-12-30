package com.taloslogy.playolapp.utils

import android.os.Build

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
object GetDeviceInfo {

    val deviceName: String? get() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer).toString() + " " + model
        }
    }

    private fun capitalize(s: String?): String? {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}