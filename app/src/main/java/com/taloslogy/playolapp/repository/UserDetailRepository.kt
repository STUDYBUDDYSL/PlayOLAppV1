package com.taloslogy.playolapp.repository

import android.util.Log
import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCClient
import java.net.URL

class UserDetailRepository {

    companion object {
        private const val AUTH_URL = "https://greentel.taloslogy.net/xmlrpc/2/common"
        private const val QUERY_URL = "https://greentel.taloslogy.net/xmlrpc/2/object"
    }

    fun authenticate(listener: XMLRPCCallback, userName: String, token: String){
        try{
            val client = XMLRPCClient(URL(AUTH_URL))
            client.callAsync(
                listener,
                "authenticate",
                "greentel", userName, token, ""
            )
        }
        catch(e: Exception){
            Log.e("TEST_LOG", e.toString())
        }
    }

    fun updateUserDetails(listener: XMLRPCCallback, id: Int, token: String, params: ArrayList<Any>){
        try{
            val client = XMLRPCClient(URL(QUERY_URL))
            client.callAsync(
                listener,
                "execute_kw",
                "greentel", id, token, "res.partner", "update_student_details", params
            )

        }
        catch(e: Exception){
            Log.e("TEST_LOG", e.toString())
        }
    }

}