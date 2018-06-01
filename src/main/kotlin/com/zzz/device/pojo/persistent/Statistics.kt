package com.zzz.device.pojo.persistent

import org.springframework.data.annotation.Id
import java.util.*

data class Statistics(@Id val id: String? = null,
                      var sn: String = "",
                      var generateTime: Long = 0L,
                      var readableDate: Date = Date(generateTime),
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

  constructor(history: History): this() {
    sn = history.sn.toUpperCase()
    switch1 = history.switch1
    switch2 = history.switch2
    switch3 = history.switch3
    switch4 = history.switch4
    ad1 = history.ad1
    ad2 = history.ad2
    ad3 = history.ad3
    ad4 = history.ad4
    voltage = history.voltage
    gpsLongitude = history.gpsLongitude
    gpsLatitude = history.gpsLatitude
    lbsLongitude = history.lbsLongitude
    lbsLatitude = history.lbsLatitude
    launchTime = history.launchTime
    battery = history.battery
    signalIntensity = history.signalIntensity
    generateTime = System.currentTimeMillis()
    readableDate = Date(generateTime)
  }
}