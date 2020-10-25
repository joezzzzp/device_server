package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.LastEndDate
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LastEndDateRepository: MongoRepository<LastEndDate, String> {
    fun findByJobMark(jobMark: String): LastEndDate?
}