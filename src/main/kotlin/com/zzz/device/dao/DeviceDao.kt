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

  fun updateDevice(device: Device) {
    val query = Query(Criteria.where("sn").`is`(device.sn))
    val update = Update().
                  set("lastUpdateTime", device.lastUpdateTime).
                  set("readableDate", device.readableDate)
    if (!StringUtils.isEmpty(device.name)) {
      update.set("name", device.name)
    }
    if (device.sumInfo != null) {
      update.set("sumInfo", device.sumInfo)
    }
    mongoTemplate.upsert(query, update, Device::class.java)
  }
}