package com.taloslogy.playolapp.utils

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile

object SafetyNet {

    val getAdder get() = arrayListOf<Int>(5, 3, 0, 2, 5, 0, 4, 3, 1, 1, 5, 0, 4, 7, 1, 5, 3, 0, 5, 1, 7, 10, 1, 3, 5, 4, 1, 3, 1, 4)

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