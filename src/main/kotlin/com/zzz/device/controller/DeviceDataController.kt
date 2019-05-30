package com.zzz.device.controller

import com.zzz.device.ApiCountUtils
import com.zzz.device.config.Config
import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.dao.ApiCountDao
import com.zzz.device.dao.DeviceDao
import com.zzz.device.dao.HistoryDao
import com.zzz.device.dao.repo.DeviceRepository
import com.zzz.device.pojo.persistent.Device
import com.zzz.device.pojo.persistent.DeviceStatus
import com.zzz.device.pojo.persistent.History
import com.zzz.device.pojo.request.AddSyncDevicesRequest
import com.zzz.device.pojo.request.ApiCountRequest
import com.zzz.device.schedule.ScheduledTasks
import com.zzz.device.service.DeviceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.TemporalField
import java.util.concurrent.ExecutorService

@RestController
class DeviceDataController {

  private val logger = LoggerFactory.getLogger(DeviceDataController::class.java)

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

  @Autowired
  private lateinit var allDeviceDao: AllDeviceDao

  @Autowired
  @Qualifier(value = "syncTaskExecutor")
  private lateinit var syncTaskExecutor: ExecutorService

  @Autowired
  private lateinit var apiCountDao: ApiCountDao

  @GetMapping("info")
  @ResponseBody
  fun query(@RequestParam(value="sn", defaultValue = "") sn: String): Device? {
    ApiCountUtils.count(ApiCountUtils.INFO_KEY)
    val upperSn = sn.toUpperCase()
    if (!StringUtils.isEmpty(sn)) {
      scheduledTask.syncOne(upperSn)
    }
    val ret = deviceRepo.findBySn(sn.toUpperCase())
    ret?.run {
      this.sn = sn.toUpperCase()
      if (sumInfo == null) {
        sumInfo = History(sn = sn.toUpperCase(),
          switch1 = -1,
          switch2 = -1,
          switch3 = -1,
          switch4 = -1,
          ad1 = -1,
          ad2 = -1,
          ad3 = -1,
          ad4 = -1,
          voltage = -1,
          gpsLatitude = -1.0,
          gpsLongitude = -1.0,
          lbsLatitude = -1.0,
          lbsLongitude = -1.0,
          launchTime = -1,
          battery = -1,
          signalIntensity = -1)
      }
    }
    return ret
  }

  @GetMapping("history")
  @ResponseBody
  fun history(@RequestParam(value = "sn", defaultValue = "") sn: String,
              @RequestParam(value = "skip", defaultValue = "0") skip: Long,
              @RequestParam(value = "limit", defaultValue = "0") limit: Int): List<History>? {
    ApiCountUtils.count(ApiCountUtils.HISTORY_KEY)
    val device = deviceRepo.findBySn(sn) ?: return arrayListOf()
    val startTime = device.startTime ?: return arrayListOf()
    return historyDao.findHistories(sn, skip, limit, startTime)
  }

  @GetMapping("updateDate")
  fun updateStartTime(@RequestParam(value = "sn", defaultValue = "") sn: String,
                      @RequestParam(value = "newTimeStamp") newTimeStamp: Long?): Map<String, Any> {
    ApiCountUtils.count(ApiCountUtils.UPDATE_TIME_KEY)
    val newDate = if (newTimeStamp != null)
      LocalDateTime.ofInstant(Instant.ofEpochMilli(newTimeStamp), Config.UTC_PLUS_8) else LocalDateTime.now()
    val device = deviceDao.findDevice(sn.toUpperCase())
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

  @GetMapping("recount")
  fun recountDevice(): Map<String, Any> {
    val allDevices = allDeviceDao.findAll()
    allDevices.forEach {
      val device = deviceDao.findDevice(it)
      device?.run {
        logger.info("sn: {}, time: {}", this.sn, this.startTime?.toInstant(Config.UTC_PLUS_8)?.toEpochMilli())
        updateStartTime(it, this.startTime?.toInstant(Config.UTC_PLUS_8)?.toEpochMilli())
      }
    }
    return mapOf("code" to 200, "message" to "success")
  }

  @GetMapping("syncService/sync")
  @ResponseBody
  fun startSync(): Map<String, Any> {
    val remainSn = scheduledTask.isSyncing()
    if (remainSn > -1) {
      return mapOf("code" to 101, "message" to "There is a running job, $remainSn sns are waiting for sync, " +
              "please try later")
    }
    syncTaskExecutor.execute { scheduledTask.sync() }
    return mapOf("code" to 200, "message" to "Start sync")
  }

  @GetMapping("syncService/syncStatus")
  fun getSyncStatus(): Map<String, Any> {
    val remainSn = scheduledTask.isSyncing()
    if (remainSn > -1) {
      return mapOf("code" to 101, "message" to "There is a running job, $remainSn sns are waiting for sync. " +
              "If you want to start a new sync job, please wait the current job finish")
    }
    return mapOf("code" to 200, "message" to "No job is running, you can start a sync job now")
  }

  @DeleteMapping("remove/{sn}")
  @ResponseBody
  fun deleteSyncDevice(@PathVariable("sn") sn: String): Map<String, Any> {
    allDeviceDao.delete(sn)
    return mapOf("code" to 200, "message" to "Delete success")
  }

  @PostMapping("add")
  @ResponseBody
  fun addSyncDevices(@RequestBody request: AddSyncDevicesRequest): Map<String, Any> {
    allDeviceDao.saveSns(request.snList)
    return mapOf("code" to 200, "message" to "Add success")
  }

  @GetMapping("findAll")
  fun findSyncDevices(): Map<String, Any> {
    return mapOf("code" to 200, "message" to "Query success", "data" to allDeviceDao.findAll())
  }

  @PostMapping("upload")
  fun resolveUploadFile(@RequestParam("file") file: MultipartFile): Map<String, Any> {
    val reader = BufferedReader(InputStreamReader(file.inputStream))
    val sns = mutableListOf<String>()
    while (true) {
      val sn = reader.readLine() ?: break
      if (sn.isNotBlank()) {
        sns.add(sn.trim())
      }
    }
    if (sns.isNotEmpty()) {
      allDeviceDao.saveSns(sns)
    }
    return mapOf("code" to 200, "message" to "Read file success")
  }

  @GetMapping("/apiCount")
  fun apiCount(@RequestBody apiCountRequest: ApiCountRequest): Map<String, Any> {
    val ret = mutableMapOf<String, Int>()
    apiCountDao.count(apiCountRequest.key, apiCountRequest.beginTime, apiCountRequest.endTime).forEach {
      val lastCount = ret[it.key] ?: 0
      ret[it.key] = it.count + lastCount
    }
    return mapOf("code" to 200, "message" to "Count success", "data" to ret)
  }
}