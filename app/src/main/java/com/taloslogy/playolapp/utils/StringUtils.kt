package com.taloslogy.playolapp.utils

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
object StringUtils {

    val grades: Array<String> = arrayOf("Select grade", "Grade 11", "Grade 10")

    val getGrade10Name:String get() = "Grade 10"

    val getGrade11Name:String get() = "Grade 11"

    val getEnglishName:String get() = "english-for-all"

    val getRevisionName:String get() = "Revision"

    var sdCardPath = ""

    val getCoursePath:String get() = "$sdCardPath/Courses"

    val getKeyFileName:String get() = "key.talos"

    val getJsonFileName:String get() = "fileNames.json"

    val districts: Array<String> = arrayOf(
        "Select district",
        "Ampara",
        "Anuradhapura",
        "Badulla",
        "Batticaloa",
        "Colombo",
        "Galle",
        "Gampaha",
        "Hambantota",
        "Jaffna",
        "Kalutara",
        "Kandy",
        "Kegalle",
        "Kilinochchi",
        "Kurunegala",
        "Mannar",
        "Matale",
        "Matara",
        "Monaragala",
        "Mullaitivu",
        "Nuwara Eliya",
        "Polonnaruwa",
        "Puttalam",
        "Ratnapura",
        "Trincomalee",
        "Vavuniya"
    )

}