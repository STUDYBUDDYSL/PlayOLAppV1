package com.taloslogy.playolapp.utils

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
object StringUtils {

    val grades: Array<String> = arrayOf("Select grade", "Grade 11", "Grade 10")

    val getGrade10Name:String get() = "Grade 10"

    val getGrade11Name:String get() = "Grade 11"

    val getEnglishName:String get() = "English"

    var sdCardPath = ""

    val getCoursePath:String get() = "$sdCardPath/Courses"

    val getKeyFileName:String get() = "key.talos"

    val getJsonFileName:String get() = "fileNames.json"

}