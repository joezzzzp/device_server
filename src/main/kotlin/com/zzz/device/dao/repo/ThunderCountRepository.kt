package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.ThunderCount
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ThunderCountRepository: MongoRepository<ThunderCount, String> {
    fun findAllBySnAndDateBetweenOrderByDate(sns: String, startDate: LocalDateTime,
                                             endDate: LocalDateTime): List<ThunderCount>
}