package com.zzz.device.dao

import com.zzz.device.dao.repo.HistoryRepository
import com.zzz.device.pojo.persistent.History
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class HistoryDao {

  @Autowired
  private lateinit var historyRepo: HistoryRepository

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun saveAll(histories: List<History>) {
    historyRepo.saveAll(histories)
  }

  fun save(history: History) {
    historyRepo.save(history)
  }

  fun findHistories(sn: String): List<History>? {
    return historyRepo.findBySn(sn)
  }

  fun findHistories(sn: String, skip: Long, limit: Int,
                    startDate: LocalDateTime = LocalDateTime.parse("1970-01-01T00:00:00")): List<History>? {
    val query = Query(Criteria.where("sn").`is`(sn).and("collectDate").gte(startDate))
            .with(Sort.by(Sort.Order.desc("collectDate"))).skip(skip).limit(limit)
    return mongoTemplate.find(query, History::class.java)
  }

  fun findLatestHistory(sn: String): History? {
    val query = Query(Criteria.where("sn").`is`(sn)).with(Sort.by(Sort.Order.desc("collectDate")))
    val histories = mongoTemplate.find(query, History::class.java)
    return if (histories.isNotEmpty()) histories[0] else null
  }

  fun findHistoriesInSomeTime(sn: String, begin: LocalDateTime, end: LocalDateTime): List<History>? {
    val query = Query(Criteria.where("sn").`is`(sn).and("collectDate").gte(begin).lte(end)).
                  with(Sort.by(Sort.Order.desc("collectDate")))
    return mongoTemplate.find(query, History::class.java)
  }
}