package com.zzz.device.controller

import com.zzz.device.dao.repo.DeviceRepository
import com.zzz.device.pojo.persistent.Device
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeviceDataController {

  @Autowired
  private lateinit var deviceRepo: DeviceRepository

  @GetMapping("info")
  @ResponseBody
  fun query(@RequestParam(value="sn", defaultValue = "") sn: String): Device? {
    return deviceRepo.findBySn(sn.toUpperCase())
  }
}