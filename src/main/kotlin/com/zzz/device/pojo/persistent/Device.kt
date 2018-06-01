package com.zzz.device.pojo.persistent

import org.springframework.data.annotation.Id
import java.util.*

data class Device(@Id val id: String? = null,
                  val sn: String = "",
                  val name: String = "",
                  val sumInfo: Statistics? = null,
                  val lastUpdateTime: Long = 0,
                  var readableDate: Date = Date(lastUpdateTime))
