package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.Statistics
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface StatisticsRepository: MongoRepository<Statistics, String> {
  fun findBySn(sn: String): List<Statistics>?
}