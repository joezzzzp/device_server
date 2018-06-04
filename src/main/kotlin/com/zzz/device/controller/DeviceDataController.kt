package com.zzz.device.controller

import com.zzz.device.config.Config
import com.zzz.device.dao.DeviceDao
import com.zzz.device.dao.HistoryDao
import com.zzz.device.dao.repo.DeviceRepository
import com.zzz.device.pojo.persistent.Device
import com.zzz.device.pojo.persistent.DeviceStatus
import com.zzz.device.pojo.persistent.History
import com.zzz.device.schedule.ScheduledTasks
import com.zzz.device.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalDateTime
import kotlin.concurrent.thread

@RestController
class DeviceDataController {

  @Autowired
  private lateinit var deviceRepo: DeviceRepository

  @Autowired
  private lateinit var scheduledTask: ScheduledTasks

  @Autowired
  private lateinit var historyDao: HistoryDao

  @Autowired
  private lateinit var deviceDao: DeviceDao

  @Autowired
  private lateinit var deviceService: DeviceService

  @GetMapping("info")
  @ResponseBody
  fun query(@RequestParam(value="sn", defaultValue = "") sn: String): Device? =
    deviceRepo.findBySn(sn.toUpperCase())

  @GetMapping("history")
  @ResponseBody
  fun history(@RequestParam(value = "sn", defaultValue = "") sn: String,
              @RequestParam(value = "skip", defaultValue = "0") skip: Long,
              @RequestParam(value = "limit", defaultValue = "0") limit: Int): List<History>? {
    val device = deviceRepo.findBySn(sn) ?: return arrayListOf()
    val startTime = device.startTime ?: return arrayListOf()
    return historyDao.findHistories(sn, skip, limit, startTime)
  }

  @GetMapping("updateDate")
  fun updateStartTime(@RequestParam(value = "sn", defaultValue = "") sn: String,
                      @RequestParam(value = "newTimeStamp") newTimeStamp: Long?): Map<String, Any> {
    val newDate = if (newTimeStamp != null)
      LocalDateTime.ofInstant(Instant.ofEpochMilli(newTimeStamp), Config.UTC_PLUS_8) else LocalDateTime.now()
    val device = deviceDao.findDevice(sn)
    device?.run {
      startTime = newDate
      status = DeviceStatus.NORMAL
      sumInfo = null
      val histories = historyDao.findHistoriesInSomeTime(sn, newDate, LocalDateTime.now())?.reversed()
      histories?.run {
        deviceService.count(device, this)
        deviceDao.saveDevice(device)
        return mapOf("code" to 200, "message" to "Update new date", "data" to device)
      }
    }
    return mapOf("code" to 100, "message" to "Something is wrong")
  }

  @GetMapping("syncService/sync")
  @ResponseBody
  fun startSync(): Map<String, Any> {
    thread(true) { scheduledTask.sync() }
    return mapOf("code" to 200, "message" to "Start sync")
  }
}