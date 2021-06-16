package com.taloslogy.playolapp.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.UserDetailRepository
import com.taloslogy.playolapp.utils.StringUtils
import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCException
import de.timroes.axmlrpc.XMLRPCServerException

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserDetailViewModel : ViewModel() {

    private val userRepo: UserDetailRepository = UserDetailRepository()

    val apiCall = MutableLiveData<LoginResult>(LoginResult(LoginRes.LoginWaiting))

    val dob = MutableLiveData<String>("")
    val address = MutableLiveData<String>("")
    val city = MutableLiveData<String>("")
    val phoneNumber = MutableLiveData<String>("")
    val school = MutableLiveData<String>("")
    val grade = MutableLiveData<Int>(0)
    val district = MutableLiveData<Int>(0)
    val gender = MutableLiveData<String>("Male")
    val consent = MutableLiveData<Boolean>(false)

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
        addSource(district) {
            value = isValidForm()
        }
        addSource(consent) {
            value = isValidForm()
        }

    }

    private fun isValidForm() : Boolean {
        return !dob.value.isNullOrBlank()
                && !city.value.isNullOrBlank()
                && !phoneNumber.value.isNullOrBlank()
                && !school.value.isNullOrBlank()
                && grade.value != 0
                && district.value != 0
                && consent.value!!
    }

    var token: String? = null

    private val authListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            if (result is Int){
                val params = arrayListOf(1, mapOf(
                    "uid" to result,
                    "grade" to StringUtils.grades[grade.value!!],
                    "dob" to dob.value,
                    "school" to school.value,
                    "province" to "",
                    "district" to StringUtils.districts[district.value!!],
                    "city" to city.value,
                    "street" to address.value,
                    "phone" to phoneNumber.value,
                    "gender" to gender.value!!.toLowerCase()
                ))
                userRepo.updateUserDetails(detailListener, result, token!!, params)
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Server error. Please try again!"))
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
        }
    }

    private val detailListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            apiCall.postValue(LoginResult(LoginRes.LoginSuccess))
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Server error. Please try again!"))
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
        }
    }

    fun updateUserDetails(userName: String, token: String){
        apiCall.postValue(LoginResult(LoginRes.LoginLoading))
        this.token = token
        userRepo.authenticate(authListener, userName, token)
    }

}
