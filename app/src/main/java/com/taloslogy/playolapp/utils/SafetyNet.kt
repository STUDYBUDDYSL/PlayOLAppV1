package com.taloslogy.playolapp.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object SafetyNet {

    fun runSafetyNet(context: Context) {
        val phoneModel = GetDeviceInfo.deviceName
        val isRooted = RootCheck.isDeviceRooted

        if(phoneModel.equals(StringUtils.getDeviceName) || isRooted){
            val uri = Uri.parse(StringUtils.getExternalStorageTreePath)
            var docFile = DocumentFile.fromTreeUri(context,uri)
            docFile = docFile?.findFile(StringUtils.getCourseFolderName)
            docFile = docFile?.findFile(StringUtils.getKeyFileName)
            docFile?.delete()
        }
    }

}