package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "device")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Device(@Id val id: String? = null,
                  var sn: String = "",
                  var name: String = "",
                  var startTime: LocalDateTime? = LocalDateTime.parse("1970-01-01T00:00:00"),
                  var updatedAt: LocalDateTime? = LocalDateTime.parse("1970-01-01T00:00:00"),
                  var status: DeviceStatus? = DeviceStatus.NORMAL,
                  var sumInfo: History? = null)

enum class DeviceStatus {
  NORMAL, ERROR, STATUS_AUTO_CHANGED
}

