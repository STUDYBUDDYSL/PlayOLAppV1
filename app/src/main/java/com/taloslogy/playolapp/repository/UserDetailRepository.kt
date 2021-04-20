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
                "greentel", "admin@taloslogy.com", "odoo@123#", ""
            )
        }
        catch(e: Exception){
            Log.e("TEST_LOG", e.toString())
        }
    }

    fun odooTest2(listener: XMLRPCCallback, id: Int){
        try{
            val client = XMLRPCClient(URL(QUERY_URL))
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

}