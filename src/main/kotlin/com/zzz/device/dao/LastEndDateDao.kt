package com.zzz.device.dao

import com.zzz.device.dao.repo.LastEndDateRepository
import com.zzz.device.pojo.persistent.LastEndDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class LastEndDateDao {

    @Autowired
    private lateinit var lastEndDateRepository: LastEndDateRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun findByJobMark(jobMark: String): LastEndDate? =
        lastEndDateRepository.findByJobMark(jobMark)

    fun save(lastEndDate: LastEndDate) {
        lastEndDateRepository.save(lastEndDate)
    }

    fun update(jobMark: String, newDate: LocalDateTime) {
        val query = Query(Criteria.where("jobMark").`is`(jobMark))
        val update = Update().
                       set("date", newDate)
        mongoTemplate.upsert(query, update, LastEndDate::class.java)
    }
}