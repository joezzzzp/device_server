package com.zzz.device.config

import com.zzz.device.interceptor.SecurityInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootConfiguration
class DeviceConfigurer : WebMvcConfigurer {

  @Autowired
  private lateinit var secureInterceptor: SecurityInterceptor

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(secureInterceptor).addPathPatterns("/**")
    super.addInterceptors(registry)
  }
}