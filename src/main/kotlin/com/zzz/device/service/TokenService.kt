package com.zzz.device.service

import com.zzz.device.config.Config
import com.zzz.device.dao.TokenDao
import com.zzz.device.pojo.persistent.Token
import com.zzz.device.pojo.request.TokenRequest
import com.zzz.device.pojo.response.TokenResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate

@Service
class TokenService {

  companion object {
    private const val MAX_RETRY_TIME = 5
    private const val PERIOD = 8L * 60L * 60L * 1000L
  }

  private val logger = LoggerFactory.getLogger(TokenService::class.java)

  @Autowired
  private lateinit var config: Config

  @Autowired
  private lateinit var tokenDao: TokenDao

  fun refreshToken(): Boolean {
    if (checkToken()) {
      return true
    }
    val url = "https://api.hizyf.com/DM-open-service/auth/getToken"
    val restTemplate = RestTemplate()
    for (i in 0..MAX_RETRY_TIME) {
      val response = restTemplate.postForObject(url, TokenRequest(config.corporateId,
        config.corporatePasswd), TokenResponse::class.java)
      if (response != null && response.respCode == Config.successCode) {
        tokenDao.updateToken(Token(token = response.token, expireTime = response.expireTime))
        logger.info("get token success!")
        Config.run { token = response.token; expireTime = response.expireTime }
        return true
      }
      logger.warn("get token failed! try again (remain retry time: ${MAX_RETRY_TIME - i})")
    }
    logger.warn("get token failed!")
    return false
  }

  private fun checkToken(): Boolean {
    return !StringUtils.isEmpty(Config.token) && Config.expireTime > System.currentTimeMillis() + PERIOD
  }
}