package com.taloslogy.playolapp.utils

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile

object SafetyNet {

    fun runSafetyNet(context: AppCompatActivity) {
        val phoneModel = GetDeviceInfo.deviceName
        val isRooted = RootCheck.isDeviceRooted

        if(!phoneModel.equals(StringUtils.getDeviceName) || isRooted){
            val uri = Uri.parse(StringUtils.getExternalStorageTreePath)
            var docFile = DocumentFile.fromTreeUri(context,uri)
            docFile = docFile?.findFile(StringUtils.getCourseFolderName)
            docFile = docFile?.findFile(StringUtils.getKeyFileName)
            docFile?.delete()
            context.finishAndRemoveTask()
        }
    }

}