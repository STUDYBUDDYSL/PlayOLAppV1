package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginRes
import com.taloslogy.playolapp.models.LoginResult
import com.taloslogy.playolapp.repository.UserDetailRepository
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

    private val authListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            apiCall.postValue(LoginResult(LoginRes.LoginSuccess))
            Log.d("TEST_RESPONSE", result.toString())
            if (result is Int){
                //userRepo.odooTest2(detailListener, result)
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Server error. Please try again!"))
            Log.e("TEST_SERVER_ERROR", error.toString())
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
            Log.e("TEST_ERROR", error.toString())
        }
    }

    private val detailListener = object : XMLRPCCallback {
        override fun onResponse(id: Long, result: Any?) {
            Log.d("TEST_RESPONSE", result.toString())
            Log.d("TEST_RESPONSE", "${result!!::class.simpleName}")
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Server error. Please try again!"))
            Log.e("TEST_SERVER_ERROR", error.toString())
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            apiCall.postValue(LoginResult(LoginRes.LoginError, "Device doesn't have an internet connection!"))
            Log.e("TEST_ERROR", error.toString())
        }
    }

    fun updateUserDetails(){
        apiCall.postValue(LoginResult(LoginRes.LoginLoading))
        userRepo.authenticate(authListener, "", "")
    }

}
