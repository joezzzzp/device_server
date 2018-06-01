package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.History
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository : MongoRepository<History, String> {
  fun findBySn(sn: String): List<History>?
}