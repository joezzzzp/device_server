package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import com.zzz.device.config.Config
import com.zzz.device.pojo.response.DateItem
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.util.StringUtils
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Document(collection = "history")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class History(@Id val id: String? = null,
              var sn: String = "",
              var collectDate: LocalDateTime? = null,
              var switch1: Int = 0,
              var switch2: Int = 0,
              var switch3: Int = 0,
              var switch4: Int = 0,
              var ad1: Int = 0,
              var ad2: Int = 0,
              var ad3: Int = 0,
              var ad4: Int = 0,
              var voltage: Int = 0,
              var gpsLongitude: Double = 0.0,
              var gpsLatitude: Double = 0.0,
              var lbsLongitude: Double = 0.0,
              var lbsLatitude: Double = 0.0,
              var launchTime: Int = 0,
              var battery: Int = 0,
              var signalIntensity: Int = 0) {

  constructor(data: DateItem): this() {
    sn = data.deviceNum.toUpperCase()
    collectDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.collectDate), Config.UTC_PLUS_8)
    data.items.forEach {
      it.hexValues.run {
        if (StringUtils.isEmpty(this)) {
          return@run
        }
        try {
          when (FiledIdEnum.getField(it.fieldId)) {
            FiledIdEnum.SWITCH1 -> switch1 = toInt()
            FiledIdEnum.SWITCH2 -> switch2 = toInt()
            FiledIdEnum.SWITCH3 -> switch3 = toInt()
            FiledIdEnum.SWITCH4 -> switch4 = toInt()
            FiledIdEnum.AD1 -> ad1 = toInt()
            FiledIdEnum.AD2 -> ad2 = toInt()
            FiledIdEnum.AD3 -> ad3 = toInt()
            FiledIdEnum.AD4 -> ad4 = toInt()
            FiledIdEnum.VOLTAGE -> voltage = toInt()
            FiledIdEnum.GPS_LONGITUDE -> gpsLongitude = toDouble()
            FiledIdEnum.GPS_LATITUDE -> gpsLatitude = toDouble()
            FiledIdEnum.LBS_LONGITUDE -> lbsLongitude = toDouble()
            FiledIdEnum.LBS_LATITUDE -> lbsLatitude = toDouble()
            FiledIdEnum.LAUNCH_TIME -> launchTime = toInt()
            FiledIdEnum.BATTERY -> battery = toInt()
            FiledIdEnum.SIGNAL_INTENSITY -> signalIntensity = toInt()
            else -> {}
          }
        } catch (e: Exception) {
          println("${it.fieldId}: ${it.hexValues}")
        }
      }
    }
  }

  enum class FiledIdEnum(val fieldId: String) {
    NONE("0000"),
    SWITCH1("152D"),
    SWITCH2("101E"),
    SWITCH3("20FC"),
    SWITCH4("2066"),
    AD1("085D"),
    AD2("0A86"),
    AD3("0AE4"),
    AD4("00AA"),
    VOLTAGE("1BE4"),
    GPS_LONGITUDE("1567"),
    GPS_LATITUDE("1CC3"),
    LBS_LONGITUDE("1EEA"),
    LBS_LATITUDE("07CC"),
    LAUNCH_TIME("2557"),
    BATTERY("1E9D"),
    SIGNAL_INTENSITY("023A");

    companion object {
      fun getField(fieldId: String): FiledIdEnum {
        values().forEach { if (it.fieldId == fieldId) return it }
        return NONE
      }
    }
  }
}
