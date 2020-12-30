package com.taloslogy.playolapp.utils

import androidx.fragment.app.FragmentActivity
import java.io.File

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class FileUtils {

    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = true): List<File> {
        val files = File(path).listFiles() ?: return emptyList()
        return files
            .filter { showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()
    }

    fun readFileText(fileName: String, context: FragmentActivity): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun checkGradeExists(grade: String): Boolean {
        return File("${StringUtils.getCoursePath}/$grade").exists()
    }

}