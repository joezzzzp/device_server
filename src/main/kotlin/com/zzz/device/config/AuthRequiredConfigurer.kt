package com.zzz.device.config

import com.zzz.device.annotation.ConditionalOnAuthSwitch
import com.zzz.device.interceptor.SecurityInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry


/**
@author zzz
@date 2019/2/20 17:21
 **/

@Configuration
@ConditionalOnAuthSwitch(needAuth = true)
class AuthRequiredConfigurer: BaseDeviceConfigurer() {

    @Autowired
    private lateinit var securityInterceptor: SecurityInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**")
    }
}