package com.zzz.device.dao

import com.zzz.device.dao.repo.UserRepository
import com.zzz.device.pojo.persistent.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository


/**
@author zzz
@date 2019/4/28 14:51
 **/

@Repository
class UserDao {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun save(user: User) {
        userRepository.save(user)
    }

    fun findById(id: String): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun findByAccount(account: String): User? {
        return userRepository.findByAccount(account)
    }

    fun findByMobile(mobile: String): User? {
        return userRepository.findByMobile(mobile)
    }

    fun updatePasswordByAccount(account: String, newPassword: String) {
        val query = Query(Criteria.where("account").`is`(account))
        val update = Update().set("password", newPassword)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }
}