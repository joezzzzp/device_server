package com.zzz.device.dao

import com.zzz.device.dao.repo.DeviceRepository
import com.zzz.device.pojo.persistent.Device
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class DeviceDao {

  @Autowired
  private lateinit var deviceRepo: DeviceRepository

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun findDevice(sn: String): Device? {
    return deviceRepo.findBySn(sn)
  }

  fun saveDevice(device: Device) {
    deviceRepo.save(device)
  }
}