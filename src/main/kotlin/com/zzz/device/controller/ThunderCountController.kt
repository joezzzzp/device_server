package com.zzz.device.controller

import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.service.ThunderCountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
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

    val logger: Logger = LoggerFactory.getLogger(ThunderCountController::class.java)

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
        if (startDateTime.isAfter(endDateTime)) {
            response.sendError(500, "invalid date")
            logger.warn("invalid date, start: {}, end: {}", start, end)
            return
        }
        val result = thunderCountService.getThunderCountData(readFile(file.inputStream), startDateTime, endDateTime)
        val excel = thunderCountService.buildExcel(result, startDateTime, endDateTime)
        response.contentType = "application/vnd.ms-excel"
        val fileName = "${start}~${end}.xls"
        response.setHeader("Content-Disposition","attachment;filename=${fileName}")
        excel.write(response.outputStream)
        response.outputStream.flush()
        response.outputStream.close()
        excel.close()
    }

    fun readFile(inputStream: InputStream): List<String> {
        val scanner = Scanner(inputStream)
        val result = mutableListOf<String>()
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (StringUtils.isEmpty(line)) {
                continue
            }
            if (line.startsWith("#")) {
                continue
            }
            result.add(line.trim())
        }
        inputStream.close()
        scanner.close()
        return result
    }
}