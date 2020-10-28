package com.zzz.device.controller

import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.service.ThunderCountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
                        @RequestParam end: String,
                        @RequestParam("file") file: MultipartFile,
                        response: HttpServletResponse) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val time = LocalTime.of(0, 0, 0)
        val startDate = LocalDate.parse(start, formatter)
        val endDate = LocalDate.parse(end, formatter)
        val startDateTime = LocalDateTime.of(startDate, time)
        val endDateTime = LocalDateTime.of(endDate.plusDays(1), time)
        val result = thunderCountService.getThunderCountData(readFile(file.inputStream), startDateTime, endDateTime)
        val excel = thunderCountService.buildExcel(result, startDateTime, endDateTime)
        response.contentType = "application/octet-stream"
        val fileName = "${start}~${end}.xls"
        response.setHeader("Content-Disposition","attachment;filename=${fileName}")
        excel.write(response.outputStream)
        excel.close()
        response.outputStream.close()
    }

    fun readFile(inputStream: InputStream): List<String> {
        val scanner = Scanner(inputStream)
        val result = mutableListOf<String>()
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.startsWith("#")) {
                continue
            }
            result.add(line)
        }
        inputStream.close()
        scanner.close()
        return result
    }
}