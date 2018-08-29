package com.zzz.device

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.zzz.device.config.Config
import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.dao.TokenDao
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDateTime

@EnableScheduling
@SpringBootApplication
class DeviceApplication : CommandLineRunner, InitializingBean, WebMvcConfigurer {

    @Autowired
    private lateinit var tokenDao: TokenDao

    @Autowired
    private lateinit var config: Config

    @Autowired
    private lateinit var allDeviceDao: AllDeviceDao

    override fun run(vararg args: String?) {
        val lastToken = tokenDao.findToken()
        lastToken?.run {
            Config.token = token
            Config.expireTime = expireTime
        }
        allDeviceDao.saveSns(config.testSn)
    }

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

    private class CustomLocalDateTimeSerializer: JsonSerializer<LocalDateTime>() {
        override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            value?.run {
                gen?.writeNumber(this.toInstant(Config.UTC_PLUS_8).toEpochMilli())
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DeviceApplication>(*args)
}
