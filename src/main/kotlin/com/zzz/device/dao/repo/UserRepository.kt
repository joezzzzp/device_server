package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


/**
@author zzz
@date 2019/4/28 14:31
 **/

@Repository
interface UserRepository: MongoRepository<User, String> {

    fun findByAccount(id: String): User

    fun findByMobile(mobile: String): User
}