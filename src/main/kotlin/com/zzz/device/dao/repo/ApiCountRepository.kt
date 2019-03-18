package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.ApiCount
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


/**
@author zzz
@date 2019/3/15 16:28
 **/

@Repository
interface ApiCountRepository : MongoRepository<ApiCount, String>