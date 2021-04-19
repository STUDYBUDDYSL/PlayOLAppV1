package com.taloslogy.playolapp.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailViewModel : ViewModel() {

    val dob = MutableLiveData<String>("")
    val address = MutableLiveData<String>("")
    val city = MutableLiveData<String>("")
    val phoneNumber = MutableLiveData<String>("")
    val school = MutableLiveData<String>("")
    val grade = MutableLiveData<Int>(0)

    val valid = MediatorLiveData<Boolean>().apply {
        addSource(dob) {
            value = isValidForm()
        }
        addSource(city) {
            value = isValidForm()
        }
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
        return !dob.value.isNullOrEmpty()
                && !city.value.isNullOrEmpty()
                && !phoneNumber.value.isNullOrEmpty()
                && phoneNumber.value?.length == 10
                && !school.value.isNullOrEmpty()
                && grade.value != 0
    }

}
