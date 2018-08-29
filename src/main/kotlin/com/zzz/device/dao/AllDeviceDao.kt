package com.zzz.device.dao

import com.zzz.device.pojo.persistent.AllDevice
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class AllDeviceDao {

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun findAll(): List<String> {
    val allSyncDevices = mongoTemplate.findAll(AllDevice::class.java)
    val ret = mutableListOf<String>()
    allSyncDevices.forEach { ret.add(it.sn) }
    return ret
  }

  fun saveSns(sns: List<String>) {
    sns.forEach { save(it) }
  }

  fun save(sn: String) {
    val query = Query(Criteria.where("sn").`is`(sn))
    val update = Update.update("sn", sn)
    mongoTemplate.upsert(query, update, AllDevice::class.java)
  }

  fun delete(sn: String) {
    val query = Query(Criteria.where("sn").`is`(sn))
    mongoTemplate.remove(query, AllDevice::class.java)
  }
}