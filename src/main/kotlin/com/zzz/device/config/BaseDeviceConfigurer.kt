package com.zzz.device.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.zzz.device.annotation.ConditionalOnAuthSwitch
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDateTime

@Configuration
@ConditionalOnAuthSwitch(needAuth = false)
class BaseDeviceConfigurer : InitializingBean, WebMvcConfigurer {

  @Autowired
  lateinit var mapper: ObjectMapper

  override fun afterPropertiesSet() {
    val module = SimpleModule()
    module.addSerializer(LocalDateTime::class.java, CustomLocalDateTimeSerializer())
    mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false)
    mapper.configure(SerializationFeature. WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.registerModule(module).registerModule(ParameterNamesModule()).registerModule(Jdk8Module()).registerModule(JavaTimeModule())
  }

  override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
    converters.forEach {
      when (it) {
        is MappingJackson2HttpMessageConverter -> it.objectMapper = mapper
      }
    }
  }

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.
      addMapping("/**").
      allowedOrigins("*").
      allowedHeaders("*").
      allowCredentials(false).
      allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
  }

  private class CustomLocalDateTimeSerializer: JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
      value?.run {
        gen?.writeNumber(this.toInstant(Config.UTC_PLUS_8).toEpochMilli())
      }
    }
  }
}