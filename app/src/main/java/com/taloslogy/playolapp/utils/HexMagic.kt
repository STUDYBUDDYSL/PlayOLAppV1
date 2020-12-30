package com.taloslogy.playolapp.utils

import com.google.protobuf.ByteString
import com.taloslogy.playolapp.PlayOLProto

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class HexMagic(str: String) {

    var keyPart = ""
    var ivPart = ""

    init {
        val baseResponse = PlayOLProto.BaseResponse.newBuilder()
            .setErrCode(0)
            .build()
        val initRequest = PlayOLProto.InitRequest.newBuilder()
            .setBaseResponse(baseResponse)
            .setChallenge(ByteString.copyFrom(GetDeviceInfo.deviceName, Charsets.UTF_8))
            .build()
        val au = PlayOLProto.InitRequest.parseFrom(initRequest.toByteArray()).challenge
        val authResponse1 = PlayOLProto.AuthResponse.newBuilder()
            .setInitRequest(initRequest)
            .setAesSessionKey(ByteString.copyFrom(str, Charsets.UTF_8))
            .build()
        authResponse1.toByteArray()

        val byteArray1 = str16ToByteArray(str.substring(0,58))
        val p1 = PlayOLProto.BaseResponse.parseFrom(byteArray1).errmsg
        val byteArray2 = str16ToByteArray(str.substring(58))
        val p2 = PlayOLProto.BaseResponse.parseFrom(byteArray2).errmsg

        if(au.toStringUtf8() == StringUtils.getDeviceName){
            keyPart = getStringFromBytes(p1)
            ivPart = getStringFromBytes(p2)
        }
    }

    private fun str16ToByteArray(str: String): ByteArray {
        val arr = arrayListOf<Int>()
        for (x in str.indices step 2){
            val s = str.substring(x, x+2)
            val s10 = s.toInt(16)
            arr.add(s10)
        }
        return arr.foldIndexed(ByteArray(arr.size)) { i, a, v -> a.apply { set(i, v.toByte()) } }
    }

    private fun getStringFromBytes(bytes: String?): String {
        var str = ""
        bytes?.forEach {
            str += it.toString()
        }
        return str
    }
}