package com.taloslogy.playolapp.utils

import com.taloslogy.playolapp.BuildConfig
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val AES_KEY = "017035012004015000026013125142159157167068063068035010145002029020158170135154155119155126"
const val AES_IV = "017035012004015000026013125142159157167068063068035010145002029014045167149060"

const val PART_KEY = "08d40f121863624257654e6473674d39555a747430695765476466733308d40f120c782b61584a7e4b415d2a7d66"

class Decryptor {

    fun decryptKey(file: File): ByteArray? {
        try {
            val aesKey = ByteMagic(AES_KEY).str
            val aesIv = ByteMagic(AES_IV).str

            val hexMagic = HexMagic(PART_KEY)

            val keyBytes = addBytesEvery4(hexMagic.keyPart, aesKey.toByteArray())
            val ivBytes = addBytesEvery4(hexMagic.ivPart, aesIv.toByteArray())
            val sks = SecretKeySpec(keyBytes, BuildConfig.KEY_SPEC)
            val iv = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance(BuildConfig.CIPHER_SPEC)
            cipher.init(Cipher.DECRYPT_MODE, sks, iv)

            // read file to byte[]
            val size = file.length().toInt()
            val fileBytes = ByteArray(size)
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(fileBytes, 0, fileBytes.size)
            buf.close()
            return cipher.doFinal(fileBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decryptVideoFile(passphrase: String, ivs: String, file: File): ByteArray? {
        try {
            val keyBytes = passphrase.trim().toByteArray()
            val ivBytes = ivs.trim().toByteArray()
            val sks = SecretKeySpec(keyBytes, BuildConfig.KEY_SPEC)
            val iv = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance(BuildConfig.CIPHER_SPEC)
            cipher.init(Cipher.DECRYPT_MODE, sks, iv)

            // read file to byte[]
            val size = file.length().toInt()
            val fileBytes = ByteArray(size)
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(fileBytes, 0, fileBytes.size)
            buf.close()

            return cipher.doFinal(fileBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Add every byte of array as 1st byte in final 4 chunk
    private fun addBytesEvery4(str: String, arr: ByteArray): ByteArray {
        val newArr = arrayListOf<Byte>()

        for (x in arr.indices) {
            newArr.add(arr[x])
            var i = 0
            while (i<3){
                newArr.add(str[(x*3)+i].toByte())
                i++
            }
        }

        return newArr.toByteArray()
    }

}