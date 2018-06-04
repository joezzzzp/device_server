package com.zzz.device.interceptor

import com.zzz.device.BusinessError
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SecurityInterceptor: HandlerInterceptor {

  private val logger = LoggerFactory.getLogger(SecurityInterceptor::class.java)

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    if (request.method.toUpperCase() == "OPTIONS") {
      return true
    }
    val authRet = request.getHeader("accessToken") == "123"
    if (!authRet) {
      handleBusinessError(response, BusinessError.INVALID_ACCESS_TOKEN)
    }
    return authRet
  }

  fun handleBusinessError(response: HttpServletResponse, error: BusinessError) {
    response.run {
      characterEncoding = "UTF-8"
      contentType = "application/json; charset=utf8"
    }
    try {
      response.writer.print(convert2String(error))
    } catch (e: IOException) {
      logger.warn("response error")
    }
  }

  fun convert2String(error: BusinessError): String {
    return "{\"code\": ${error.code},\"message\": \"${error.message}\"}"
  }
}