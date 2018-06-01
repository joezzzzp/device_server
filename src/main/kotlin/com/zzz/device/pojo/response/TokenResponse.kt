package com.zzz.device.pojo.response

data class TokenResponse(val respCode: String = "",
                         val respMessage: String = "",
                         val token: String = "",
                         val expireTime: Long = 0)