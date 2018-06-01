package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.Device
import org.springframework.data.mongodb.repository.MongoRepository

interface DeviceRepository : MongoRepository<Device, String> {
  fun findBySn(sn: String): Device?
}