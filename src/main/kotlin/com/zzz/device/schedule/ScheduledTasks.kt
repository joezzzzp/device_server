package com.zzz.device.schedule

import com.zzz.device.annotation.TokenRefresh
import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.service.DeviceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class ScheduledTasks {
  private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  @Autowired
  private lateinit var deviceService: DeviceService

  @Autowired
  private lateinit var allDeviceDao: AllDeviceDao

  @Scheduled(cron = "0 0 * * * *")
  @TokenRefresh
  fun sync() {
    logger.info("start sync at {}", dateFormat.format(Date()))
    deviceService.run {
      getDevicesInfo(allDeviceDao.findAll())
    }
    logger.info("end sync at {}", dateFormat.format(Date()))
  }

  /**
   * Return the number of sn to be synced.
   * 0 means no sync task is running, we can start a new one
   */
  fun isSyncing(): Int {
    if (!deviceService.isSyncing) {
      return -1
    }
    return deviceService.count
  }

  @TokenRefresh
  fun syncOne(sn: String) {
    logger.info("start sync at {}", dateFormat.format(Date()))
    deviceService.run {
      val sns = listOf(sn)
      getDevicesInfo(sns)
    }
    logger.info("end sync at {}", dateFormat.format(Date()))
  }
}