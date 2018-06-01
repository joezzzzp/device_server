package com.zzz.device.pojo.persistent

import org.springframework.data.annotation.Id
import java.util.*

data class Token(@Id private val id: String? = null,
                 private val dataType: String = "token",
                 val token: String = "",
                 val expireTime: Long = 0,
                 var readableDate: Date = Date(expireTime)) {

  override fun toString(): String {
    return "{id = $id, dataType = $dataType, token = $token, expireTime = $expireTime, readableDate = $readableDate]}"
  }
}

