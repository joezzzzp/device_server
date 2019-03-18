package com.zzz.device.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
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
import java.time.format.DateTimeFormatter

@Configuration
@ConditionalOnAuthSwitch(needAuth = false)
class BaseDeviceConfigurer : InitializingBean, WebMvcConfigurer {

  companion object {
      private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  }

  @Autowired
  lateinit var mapper: ObjectMapper

  override fun afterPropertiesSet() {
    val module = SimpleModule()
    module.addSerializer(LocalDateTime::class.java, CustomLocalDateTimeSerializer())
    module.addDeserializer(LocalDateTime::class.java, CustomLocalDateTimeDeserializer())
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

  private class CustomLocalDateTimeDeserializer: JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
      return LocalDateTime.parse(p?.valueAsString ?: "", dateTimeFormatter)
    }
  }
}