package com.zzz.device.aop

import com.zzz.device.service.TokenService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
    @author zzz
    @date 2019/2/20 14:51
 **/

@Aspect
@Component
class TokenAspect {

    private val logger = LoggerFactory.getLogger(TokenAspect::class.java)

    @Autowired
    private lateinit var tokenService: TokenService

    @Pointcut("@annotation(com.zzz.device.annotation.TokenRefresh)")
    fun refreshTokenJoinPoint() {
        // refresh token join point, empty implement
    }

    @Around("refreshTokenJoinPoint()")
    fun refreshToken(pjp: ProceedingJoinPoint) {
        if (tokenService.refreshToken()) {
            logger.info("refresh token success!")
            pjp.proceed()
        } else {
            logger.error("get token failed, stop execution of method \"${(pjp.signature as MethodSignature).method.name}\"")
        }
    }
}