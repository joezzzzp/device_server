package com.zzz.device.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "config")
class Config {
  companion object {
    const val successCode = "00000000"
    var token = ""
    var expireTime = -1L
  }

  lateinit var corporateId: String
  lateinit var corporatePasswd: String
  var testSn: List<String> = listOf()

}
