package com.taloslogy.playolapp.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailViewModel : ViewModel() {

    val address = MutableLiveData<String>("")
    val phoneNumber = MutableLiveData<String>("")
    val school = MutableLiveData<String>("")
    val grade = MutableLiveData<Int>(0)

    val valid = MediatorLiveData<Boolean>().apply {
        addSource(phoneNumber) {
            value = isValidForm()
        }
        addSource(school) {
            value = isValidForm()
        }
        addSource(grade) {
            value = isValidForm()
        }

    }

    private fun isValidForm() : Boolean {
        return !phoneNumber.value.isNullOrEmpty()
                && !school.value.isNullOrEmpty()
                && grade.value != 0
    }

}
