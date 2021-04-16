package com.taloslogy.playolapp.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taloslogy.playolapp.models.LoginPayload
import com.taloslogy.playolapp.repository.LoginRepository
import com.taloslogy.playolapp.utils.storage.PrefHelper
import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCException
import de.timroes.axmlrpc.XMLRPCServerException
import java.net.URL

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class UserViewModel(private val prefs: PrefHelper) : ViewModel() {

    var name: String? = null
    var email: String? = null

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val loginCycle = MutableLiveData<LoginPayload>(LoginPayload.LoginWaiting)

    private val loginRepo: LoginRepository = LoginRepository()

    fun checkLogin() : LiveData<Boolean> {
        checkForKeys()
        return _userLoggedIn
    }

    private fun checkForKeys() {
        _userLoggedIn.value = prefs.userPref.get()
    }

    fun loginComplete() {

        // TODO(Get the activation codes from a network call)

        // Set the keys retrieved from api
        prefs.key1Pref.set("wrmffxYB")
        prefs.key2Pref.set("GPVSIrun")
        prefs.iv1Pref.set("d=Z~")
        prefs.iv2Pref.set("fGe+")

        // Complete login flow and not show a second time
        prefs.userPref.set(true)
    }

    private val listener = object : XMLRPCCallback{
        override fun onResponse(id: Long, result: Any?) {
            Log.d("TEST_RESPONSE", result.toString())
            Log.d("TEST_RESPONSE", "${result!!::class.simpleName}")
            if (result is Int){
                odooTest2(result)
            }
            if (result is Array<*>){
                Log.d("TEST_RESPONSE", result.size.toString())
            }
        }

        override fun onServerError(id: Long, error: XMLRPCServerException?) {
            Log.e("TEST_SERVER_ERROR", error.toString())
        }

        override fun onError(id: Long, error: XMLRPCException?) {
            Log.e("TEST_ERROR", error.toString())
        }

    }

    fun odooTest(){
        try{
            val client = XMLRPCClient(URL("http://jack-frost.taloslogy.net:8069/xmlrpc/2/common"))
            client.callAsync(listener, "authenticate", "jack_frost", "admin@taloslogy.com", "odoo@123#", "")

        }
        catch(e: Exception){
            Log.e("TEST_LOG", e.toString())
        }
    }

    fun odooTest2(id: Int){
        try{
            val client = XMLRPCClient(URL("http://jack-frost.taloslogy.net:8069/xmlrpc/2/object"))
            client.callAsync(listener, "execute_kw", "jack_frost", id,
                "odoo@123#",
                "olympus.device",
                "search_read",
                emptyList<String>(),
                mapOf("fields" to arrayListOf("display_name", "thingId"))
            )

        }
        catch(e: Exception){
            Log.e("TEST_LOG", e.toString())
        }
    }

    fun googleSignIn(fName: String, lName: String, email: String, token: String) {
        this.name = "$fName $lName"
        this.email = email

        loginCycle.postValue(LoginPayload.LoginLoading)
        try{
            Log.d("TEST_LOG", token)
            // Complete SSO login
            loginRepo.ssoLoginRequest(token) { result ->
                if(result){
                    Log.d("TEST_LOG", "Navigate...")
                    loginCycle.postValue(LoginPayload.LoginSuccess)
                }
                else {
                    //TODO display error
                    loginCycle.postValue(LoginPayload.LoginError)
                }
            }
        }
        catch(e: Exception){
            Log.e("TEST_LOG_E", e.toString())
            loginCycle.postValue(LoginPayload.LoginError)
        }
    }

}