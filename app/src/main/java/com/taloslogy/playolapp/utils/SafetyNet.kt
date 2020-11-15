package com.taloslogy.playolapp.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object SafetyNet {

    fun runSafetyNet(context: Context) {
        val phoneModel = GetDeviceInfo.deviceName
        val isRooted = RootCheck.isDeviceRooted

        if(!phoneModel.equals("Greentel V2") || isRooted){
            var docFile = DocumentFile.fromTreeUri(context,
                Uri.parse("content://com.android.externalstorage.documents/tree/5E71-DBAD%3ACourses"))
            docFile = docFile!!.findFile("key.talos")
            docFile?.delete()
        }
    }

}