package com.zzz.device.dao

import com.zzz.device.dao.repo.ThunderCountRepository
import com.zzz.device.pojo.persistent.LastEndDate
import com.zzz.device.pojo.persistent.ThunderCount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ThunderCountDao {

    @Autowired
    private lateinit var thunderCountRepository: ThunderCountRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun saveOne(thunderCount: ThunderCount) {
        thunderCountRepository.save(thunderCount)
    }

    fun save(thunderCounts: List<ThunderCount>) {
        thunderCountRepository.saveAll(thunderCounts)
    }

    fun findAllBySnAndDateBetweenOrderByDate(sn: String, startDate: LocalDateTime,
                                             endDate: LocalDateTime): List<ThunderCount>? {
        val query = Query(Criteria.where("sn").`is`(sn).and("date").gte(startDate).lt(endDate))
                .with(Sort.by(Sort.Order.asc("date")))
        return mongoTemplate.find(query, ThunderCount::class.java)
    }

}