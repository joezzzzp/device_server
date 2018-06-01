package com.zzz.device.dao

import com.zzz.device.dao.repo.TokenRepository
import com.zzz.device.pojo.persistent.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TokenDao {

  @Autowired
  private lateinit var tokenRepo: TokenRepository

  @Autowired
  private lateinit var mongoTemplate: MongoTemplate

  fun findToken(): Token? {
    return tokenRepo.findByDataType("token")
  }

  fun updateToken(token: Token) {
    val query = Query(Criteria.where("dataType").`is`("token"))
    val update = Update().
                  set("token", token.token).
                  set("expireTime", token.expireTime).
                  set("readableDate", token.readableDate)
    mongoTemplate.upsert(query, update, Token::class.java)
  }
}