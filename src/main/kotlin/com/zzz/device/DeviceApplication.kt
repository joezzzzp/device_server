package com.zzz.device

import com.zzz.device.config.Config
import com.zzz.device.dao.TokenDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class DeviceApplication : CommandLineRunner {

    @Autowired
    private lateinit var tokenDao: TokenDao

    override fun run(vararg args: String?) {
        val lastToken = tokenDao.findToken()
        lastToken?.run {
            Config.token = token
            Config.expireTime = expireTime
        }
    }

}

fun main(args: Array<String>) {
    runApplication<DeviceApplication>(*args)
}
