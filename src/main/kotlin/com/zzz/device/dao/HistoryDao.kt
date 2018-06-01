package com.zzz.device.dao

import com.zzz.device.dao.repo.HistoryRepository
import com.zzz.device.pojo.persistent.History
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class HistoryDao {

  @Autowired
  private lateinit var historyRepo: HistoryRepository

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun save(histories: List<History>) {
    historyRepo.saveAll(histories)
  }

  fun findHistories(sn: String): List<History>? {
    return historyRepo.findBySn(sn)
  }

  fun findHistoriesInSomeTime(sn: String, begin: Long, end: Long): List<History>? {
    val query = Query(Criteria.where("sn").`is`(sn).and("collectDate").gt(begin).lt(end)).
                  with(Sort.by(Sort.Order.desc("collectDate")))
    return mongoTemplate.find(query, History::class.java)
  }
}