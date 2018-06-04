package com.zzz.device.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
@ConfigurationProperties(prefix = "config")
@RefreshScope
class Config {
  companion object {
    const val successCode = "00000000"
    var token = ""
    var expireTime = -1L
    val UTC_PLUS_8 = ZoneOffset.of("+8")
  }

  lateinit var corporateId: String
  lateinit var corporatePasswd: String
  var testSn: List<String> = listOf()
  lateinit var marker: String
  var intervalInMs: Long = 60000
}
