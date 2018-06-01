package com.zzz.device.schedule

import com.zzz.device.config.Config
import com.zzz.device.service.DeviceService
import com.zzz.device.service.TokenService
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
  private lateinit var tokenService: TokenService

  @Autowired
  private lateinit var deviceService: DeviceService

  @Autowired
  private lateinit var config: Config

  @Scheduled(cron = "0/10 * * * * *")
  fun sync() {
    logger.info("start sync at {}", dateFormat.format(Date()))
    if (tokenService.refreshToken()) {
      deviceService.run {
        getDeviceInfos(config.testSn)
        countStatistics(config.testSn)
      }
      logger.info("sync success!")
    } else {
      logger.info("sync failed!")
    }
    logger.info("end sync at {}", dateFormat.format(Date()))
  }
}