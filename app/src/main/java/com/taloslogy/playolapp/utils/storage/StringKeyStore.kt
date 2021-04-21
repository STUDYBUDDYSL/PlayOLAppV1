package com.taloslogy.playolapp.utils.storage

import android.content.SharedPreferences
import android.util.Log
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
open class StringKeyStore @JvmOverloads constructor(private val preferences: SharedPreferences, private val key: String, private val defaultValue: String? = null) {
    fun get(): String? {
        var value: String? = null
        try {
            // Get decrypt key from shared preference
            val decryptKey = preferences.getString(key, defaultValue)
            // decrypt key is not null, decrypt the key alias from keystore and set the decrypted value to val
            if (decryptKey != null) {
                val cryptography = Crypto(key)
                value = cryptography.decrypt(decryptKey)
            }
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: AssertionError) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return value
    }

    // If preference has decrypt key, then all good
    val isSet: Boolean
        get() {
            // If preference has decrypt key, then all good
            return preferences.contains(key)
        }

    fun set(value: String) {
        try {
            // Get keystore instance with key
            val cryptography = Crypto(key)
            // Get decryption public key after encryption
            val decryptionKey: String = cryptography.encrypt(value)
            // Save decryption key into shared preference for retrieval
            preferences.edit().putString(key, decryptionKey).apply()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
    }

    fun delete() {
        try {
            // Get keystore instance with key
            val cryptography = Crypto(key)
            // Delete key in store with the alias: 'key'
            cryptography.delete(key)
            // Delete shared preference decryption key to indicate loss of key
            preferences.edit().remove(key).apply()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

}