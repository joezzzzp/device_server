package com.zzz.device.dao

import com.zzz.device.dao.repo.StatisticsRepository
import com.zzz.device.pojo.persistent.Statistics
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class StatisticsDao {

  @Autowired
  private lateinit var statisticsRepo: StatisticsRepository

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun findBySn(sn: String): List<Statistics>? {
    return statisticsRepo.findBySn(sn)
  }

  fun findLatestBySn(sn: String): Statistics? {
    val query = Query(Criteria.where("sn").`is`(sn)).with(Sort.by(Sort.Order.desc("generateTime")))
    return mongoTemplate.findOne(query, Statistics::class.java)
  }

  fun save(data: Statistics) {
    statisticsRepo.save(data)
  }
}