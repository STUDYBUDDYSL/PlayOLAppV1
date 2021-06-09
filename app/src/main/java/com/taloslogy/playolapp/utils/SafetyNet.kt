package com.taloslogy.playolapp.utils

import androidx.appcompat.app.AppCompatActivity
import com.taloslogy.playolapp.utils.storage.PrefHelper

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
object SafetyNet {

    val getAdder get() = arrayListOf<Int>(5, 3, 0, 2, 5, 0, 4, 3, 1, 1, 5, 0, 4, 7, 1, 5, 3, 0, 5, 1, 7, 10, 1, 3, 5, 4, 1, 3, 1, 4)

    fun runSafetyNet(context: AppCompatActivity) {
        val isRooted = RootCheck.isDeviceRooted

        if(isRooted){
            val pref = PrefHelper.getInstance(context)
            pref.clearPrefs()
            context.finishAndRemoveTask()
        }
    }

}