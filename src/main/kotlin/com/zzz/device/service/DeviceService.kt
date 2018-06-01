package com.zzz.device.service

import com.zzz.device.config.Config
import com.zzz.device.dao.DeviceDao
import com.zzz.device.dao.HistoryDao
import com.zzz.device.dao.StatisticsDao
import com.zzz.device.pojo.persistent.Device
import com.zzz.device.pojo.persistent.History
import com.zzz.device.pojo.persistent.Statistics
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
import java.util.*
import kotlin.collections.ArrayList

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
  private lateinit var statisticsDao: StatisticsDao

  @Autowired
  private lateinit var config: Config

  fun getDeviceInfos(sns: List<String>) {
    val url = String.format(DATE_INFO_URL_TEMPLATE, config.corporateId)
    sns.forEach {
      val device = deviceDao.findDevice(it)
      val request = DeviceInfoRequest(deviceSn = it, endDate = System.currentTimeMillis())
      request.beginDate = device?.lastUpdateTime ?: 0L
      getDeviceInfo(url, request)
    }
  }

  fun countStatistics(sns: List<String>) {
    sns.forEach {
      var statistics = statisticsDao.findLatestBySn(it)
      val start = statistics?.generateTime ?: 0L
      val end = System.currentTimeMillis()
      val histories = historyDao.findHistoriesInSomeTime(it, start, System.currentTimeMillis())
      if (histories != null && histories.isNotEmpty()) {
        var baseSwitch1 = 0; var baseSwitch2 = 0; var baseSwitch3 = 0
        if (statistics != null) {
          baseSwitch1 = statistics.switch1
          baseSwitch2 = statistics.switch2
          baseSwitch3 = statistics.switch3
        }
        statistics = Statistics(histories[0]).apply {
          switch1 = baseSwitch1 + if (switch1 == 0) 1 else 0
          switch2 = baseSwitch2 + if (switch1 == 0) 1 else 0
          switch3 = baseSwitch3 + if (switch1 == 0) 1 else 0
        }
        if (histories.size > 1) {
          val remain = histories.subList(1, histories.size)
          remain.forEach {
            statistics.run {
              switch1 += if (it.switch1 == 0) 1 else 0
              switch2 += if (it.switch2 == 0) 1 else 0
              switch3 += if (it.switch3 == 0) 1 else 0
            }
          }
        }
        statisticsDao.save(statistics)
        deviceDao.updateDevice(Device(sn = it, lastUpdateTime = System.currentTimeMillis(), sumInfo = statistics))
      }
      logger.info("count \"{}\" data during \"{} ~ {}\"", it, dateFormat.format(Date(start)), dateFormat.format(Date(end)))
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
        deviceDao.updateDevice(Device(sn = request.deviceSn, lastUpdateTime = request.endDate))
        historyDao.save(dateItems2Histories(response.t))
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
}