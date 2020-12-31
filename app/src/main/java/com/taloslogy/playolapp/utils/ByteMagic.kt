package com.taloslogy.playolapp.utils

import com.google.protobuf.ByteString
import com.taloslogy.playolapp.PlayOLProto

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class ByteMagic(var str: String) {

    init {
        val baseResponse = PlayOLProto.BaseResponse.newBuilder()
            .setErrCode(0)
            .build()
        val initRequest = PlayOLProto.InitRequest.newBuilder()
            .setBaseResponse(baseResponse)
            .setChallenge(ByteString.copyFrom(GetDeviceInfo.deviceName, Charsets.UTF_8))
            .build()

        val au = PlayOLProto.InitRequest.parseFrom(initRequest.toByteArray()).challenge

        val byteArray = getPass(str.chunked(3), devBytes = au.toByteArray())
        val authResponse = PlayOLProto.AuthResponse.newBuilder()
            .setInitRequest(initRequest)
            .setAesSessionKey(ByteString.copyFrom(str, Charsets.UTF_8))
            .build()
        authResponse.toByteArray()

        val p1 = PlayOLProto.AuthResponse.parseFrom(byteArray).aesSessionKey
        if(au.toStringUtf8() == StringUtils.getDeviceName){
            str = p1.toStringUtf8()
        }
    }

    private fun <T> concatenate(vararg lists: List<T>): List<T> {
        return listOf(*lists).flatten()
    }

    private fun getPass(list: List<String>, devBytes: ByteArray): ByteArray {
        val strs = getBytesAsString(list)
        val devAsBytes = devBytes.map { it.toString(8) }
        val final = concatenate(strs.subList(0,18), devAsBytes, strs.subList(18,strs.size))
        return final.map {it.toByte(8)}.toByteArray()
    }

    private fun getBytesAsString(list: List<String>): ArrayList<String> {
        val adder = SafetyNet.getAdder
        val dedArr = arrayListOf<String>()
        for(i in list.indices){
            dedArr.add((list[i].toInt() - adder[i]).toString().padStart(3, '0'))
        }
        return dedArr
    }
}