package com.taloslogy.playolapp.utils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Decryptor {

    fun decryptKey(file: File): ByteArray? {
        try {
            val keyBytes = byteArrayOf(0x6f, 0x63, 0x62, 0x42, 0x77, 0x57, 0x65, 0x4e, 0x58, 0x64, 0x73, 0x67, 0x68, 0x4d, 0x39, 0x55, 0x6c, 0x5a, 0x74, 0x74, 0x4e, 0x30, 0x69, 0x57, 0x6c, 0x65, 0x47, 0x64, 0x52, 0x66, 0x73, 0x33)
            val ivBytes = byteArrayOf(0x24, 0x78, 0x2b, 0x61, 0x74, 0x58, 0x4a, 0x7e, 0x64, 0x4b, 0x41, 0x5d, 0x2e, 0x2a, 0x7d, 0x66)
            val sks = SecretKeySpec(keyBytes, "AES")
            val iv = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, sks, iv)
            println("reading encrypted file")

            // read file to byte[]
            val size = file.length().toInt()
            val fileBytes = ByteArray(size)
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(fileBytes, 0, fileBytes.size)
            buf.close()
            println("got file bytes")
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
//            val keyBytes = byteArrayOf(0x61, 0x73, 0x64, 0x66, 0x31, 0x32, 0x33, 0x34, 0x61, 0x73, 0x64, 0x66, 0x31, 0x32, 0x33, 0x34)
//            val ivBytes = byteArrayOf(0x61, 0x73, 0x64, 0x66, 0x31, 0x32, 0x33, 0x34, 0x61, 0x73, 0x64, 0x66, 0x31, 0x32, 0x33, 0x34)
            println("LENGTHS: ${keyBytes.size} ${ivBytes.size}")
            val sks = SecretKeySpec(keyBytes, "AES")
            val iv = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, sks, iv)
            println("reading encrypted file")

            // read file to byte[]
            val size = file.length().toInt()
            val fileBytes = ByteArray(size)
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(fileBytes, 0, fileBytes.size)
            buf.close()
            println("got file bytes")
            return cipher.doFinal(fileBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}