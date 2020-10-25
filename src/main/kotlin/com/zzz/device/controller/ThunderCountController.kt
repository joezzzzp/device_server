package com.zzz.device.controller

import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.pojo.persistent.ThunderCount
import com.zzz.device.service.ThunderCountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/thunderCount")
class ThunderCountController {

    @Autowired
    private lateinit var thunderCountService: ThunderCountService

    @Autowired
    private lateinit var allDeviceDao: AllDeviceDao

    @PostMapping("/sync")
    fun startSync() {
        thunderCountService.sync()
    }

    @PostMapping("/get")
    fun getThunderCount(@RequestParam start: String,
                        @RequestParam end: String): Map<String, List<ThunderCount>?> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val time = LocalTime.of(0, 0, 0)
        val startDate = LocalDate.parse(start, formatter)
        val endDate = LocalDate.parse(end, formatter)
        val startDateTime = LocalDateTime.of(startDate, time)
        val endDateTime = LocalDateTime.of(endDate.plusDays(1), time)
        return thunderCountService.getThunderCountData(allDeviceDao.findAll(), startDateTime, endDateTime)
    }
}