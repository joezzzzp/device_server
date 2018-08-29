package com.zzz.device.config

import com.zzz.device.interceptor.SecurityInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.web.servlet.config.annotation.*

@SpringBootConfiguration
class DeviceConfigurer : WebMvcConfigurer {

  @Autowired
  private lateinit var secureInterceptor: SecurityInterceptor

  override fun addInterceptors(registry: InterceptorRegistry) {
//    registry.addInterceptor(secureInterceptor).addPathPatterns("/**")
  }

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.
      addMapping("/**").
      allowedOrigins("*").
      allowedHeaders("*").
      allowCredentials(false).
      allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
  }
}