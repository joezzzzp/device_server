package com.zzz.device.service

import com.zzz.device.config.Config
import com.zzz.device.dao.DeviceDao
import com.zzz.device.dao.HistoryDao
import com.zzz.device.pojo.persistent.Device
import com.zzz.device.pojo.persistent.DeviceStatus
import com.zzz.device.pojo.persistent.History
import com.zzz.device.pojo.request.DeviceInfoRequest
import com.zzz.device.pojo.response.DateItem
import com.zzz.device.pojo.response.DeviceInfoResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class DeviceService {

  companion object {
    private const val DATE_INFO_URL_TEMPLATE = "https://api.hizyf.com/DM-open-service/service/getByDate/%s"
    private const val MAX_RETRY_TIMES = 5
  }

  private val logger = LoggerFactory.getLogger(DeviceService::class.java)
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  @Autowired
  private lateinit var deviceDao: DeviceDao

  @Autowired
  private lateinit var historyDao: HistoryDao

  @Autowired
  private lateinit var config: Config

  fun getDevicesInfo(sns: List<String>) {
    val url = String.format(DATE_INFO_URL_TEMPLATE, config.corporateId)
    sns.forEach {
      val device = deviceDao.findDevice(it)
      val request = DeviceInfoRequest(deviceSn = it, endDate = LocalDateTime.now().toInstant(Config.UTC_PLUS_8).toEpochMilli())
      request.beginDate = device?.updatedAt?.toInstant(Config.UTC_PLUS_8)?.toEpochMilli() ?: 0L
      getDeviceInfo(url, request)
    }
  }

  private fun getDeviceInfo(url: String, request: DeviceInfoRequest) {
    val restTemplate = RestTemplate()
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      add("token", Config.token)
    }
    val requestEntity = HttpEntity(request, headers)
    for (i in 0..MAX_RETRY_TIMES) {
      val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DeviceInfoResponse::class.java).body
      if (response != null && response.respCode == Config.successCode) {
        val handledHistories = handleHistory(request.deviceSn, dateItems2Histories(response.t).reversed())
        var currentDevice = deviceDao.findDevice(request.deviceSn)
        if (currentDevice == null) {
          currentDevice = Device(sn = request.deviceSn)
        }
        count(currentDevice, handledHistories)
        currentDevice.apply {
          updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.endDate), Config.UTC_PLUS_8)
        }
        val currentDbData = deviceDao.findDevice(request.deviceSn)
        if (currentDbData == null) {
          deviceDao.saveDevice(currentDevice)
        } else {
          if (currentDbData.updatedAt!!.isBefore(currentDevice.updatedAt)) {
            deviceDao.saveDevice(currentDevice)
          }
        }
        logger.info("get \"{}\" data during \"{} ~ {}\"", request.deviceSn, dateFormat.format(Date(request.beginDate)),
          dateFormat.format(Date(request.endDate)))
        return
      }
      logger.warn("get \"{}\" data failed! try again (remain retry time: ${MAX_RETRY_TIMES - i})", request.deviceSn)
    }
    logger.warn("get \"{}\" data failed!", request.deviceSn)
  }

  private fun dateItems2Histories(dateItems: List<DateItem>): ArrayList<History> {
    val ret = arrayListOf<History>()
    dateItems.forEach { ret.add(History(it)) }
    return ret
  }

  //传入历史记录应按照时间升序排列
  private fun handleHistory(sn: String, histories: List<History>): List<History> {
    val ret: ArrayList<History> = arrayListOf()
    if (histories.isNotEmpty()) {
      var latestHistory = historyDao.findLatestHistory(sn)
      for (i in histories.indices) {
        if (histories[i].collectDate == null) {
          continue
        }
        val item = histories[i]
        if (latestHistory == null) {
          latestHistory = item
          if (i == histories.size - 1) {
            historyDao.save(latestHistory)
            ret.add(latestHistory)
          }
          continue
        }
        if (item.collectDate!!.toInstant(Config.UTC_PLUS_8).toEpochMilli() -
            latestHistory.collectDate!!.toInstant(Config.UTC_PLUS_8).toEpochMilli() > config.intervalInMs) {
          historyDao.save(latestHistory)
          ret.add(latestHistory)
          latestHistory = item
        } else {
          latestHistory.switch1 = handleSwitch(latestHistory.switch1, item.switch1)
          latestHistory.switch2 = handleSwitch(latestHistory.switch2, item.switch2)
          latestHistory.switch3 = handleSwitch(latestHistory.switch3, item.switch3)
          latestHistory.switch4 = handleSwitch(latestHistory.switch4, item.switch4)
          latestHistory.collectDate = item.collectDate
        }
        if (i == histories.size - 1) {
          historyDao.save(latestHistory)
          ret.add(latestHistory)
        }
      }
    }
    return ret
  }

  private fun handleSwitch(base: Int, new: Int): Int = if (base != 0) ( if (new != 0) 1 else 0 ) else 0

  //传入的历史记录应按照时间升序排列
  fun count(device: Device?, histories: List<History>) {
    if (histories.isEmpty()) {
      return
    }
    device?.run {
      val lastStatus = status
      val reversedHistories = histories.reversed()
      val startDate = this.startTime ?: return
      var sumInfo = this.sumInfo
      var findingFirst = true
      for (i in reversedHistories.indices) {
        val item = reversedHistories[i]
        if (item.collectDate!!.isBefore(startDate)) {
          continue
        }
        if (findingFirst) {
          if (sumInfo == null) {
            sumInfo = item.apply {
              switch1 = if (switch1 == 0) 1 else 0
              switch2 = if (switch2 == 0) 1 else 0
              switch3 = if (switch3 == 0) 1 else 0
            }
          } else {
            sumInfo.run {
              ad1 = item.ad1
              ad2 = item.ad2
              ad3 = item.ad3
              ad4 = item.ad4
              voltage = item.voltage
              gpsLatitude = item.gpsLatitude
              gpsLongitude = item.gpsLongitude
              lbsLatitude = item.lbsLatitude
              lbsLongitude = item.lbsLongitude
              launchTime = item.launchTime
              battery = item.battery
              signalIntensity = item.signalIntensity
              switch4 = item.switch4

              switch1 += if (item.switch1 == 0) 1 else 0
              switch2 += if (item.switch2 == 0) 1 else 0
              switch3 += if (item.switch3 == 0) 1 else 0
            }
          }
          findingFirst = false
          continue
        }
        sumInfo?.run {
          switch1 += if (item.switch1 == 0) 1 else 0
          switch2 += if (item.switch2 == 0) 1 else 0
          switch3 += if (item.switch3 == 0) 1 else 0
        }
      }
      this.sumInfo = sumInfo
      val currentStatus = if(this.sumInfo!!.switch4 == 0) DeviceStatus.ERROR else DeviceStatus.NORMAL
      status = if (currentStatus == DeviceStatus.ERROR) {
        DeviceStatus.ERROR
      } else {
        if (lastStatus == DeviceStatus.ERROR || lastStatus == DeviceStatus.STATUS_AUTO_CHANGED) {
          DeviceStatus.STATUS_AUTO_CHANGED
        } else {
          DeviceStatus.NORMAL
        }
      }

    }
  }
}