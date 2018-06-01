package com.zzz.device.dao.repo

import com.zzz.device.pojo.persistent.Token
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : MongoRepository<Token, String> {
  fun findByDataType(type: String?): Token?
}