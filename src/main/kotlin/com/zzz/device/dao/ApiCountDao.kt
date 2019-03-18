package com.zzz.device.dao

import com.zzz.device.dao.repo.ApiCountRepository
import com.zzz.device.pojo.persistent.ApiCount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils
import java.time.LocalDateTime


/**
@author zzz
@date 2019/3/15 17:47
 **/
@Repository
class ApiCountDao {

    @Autowired
    private lateinit var apiCountRepository: ApiCountRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun save(apiCount: ApiCount) {
        apiCountRepository.save(apiCount)
    }

    fun count(key: String?, beginTime: LocalDateTime?, endTime: LocalDateTime?): List<ApiCount> {
        val realBeginTime: LocalDateTime = beginTime ?: LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        val realEndTime: LocalDateTime = endTime ?: LocalDateTime.now()
        val criteria = Criteria.where("saveTime").gte(realBeginTime).lte(realEndTime)
        if (!StringUtils.isEmpty(key)) {
            criteria.and("key").`is`(key)
        }
        val query = Query(criteria)
        return mongoTemplate.find(query, ApiCount::class.java)
    }
}