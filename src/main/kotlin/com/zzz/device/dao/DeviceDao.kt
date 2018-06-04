package com.zzz.device.dao

import com.zzz.device.dao.repo.DeviceRepository
import com.zzz.device.pojo.persistent.Device
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils

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