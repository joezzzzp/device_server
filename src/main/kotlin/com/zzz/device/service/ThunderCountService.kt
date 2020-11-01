package com.zzz.device.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zzz.device.annotation.TokenRefresh
import com.zzz.device.config.Config
import com.zzz.device.config.Constant
import com.zzz.device.dao.AllDeviceDao
import com.zzz.device.dao.LastEndDateDao
import com.zzz.device.dao.ThunderCountDao
import com.zzz.device.pojo.persistent.History
import com.zzz.device.pojo.persistent.LastEndDate
import com.zzz.device.pojo.persistent.ThunderCount
import com.zzz.device.pojo.request.DeviceInfoRequest
import com.zzz.device.pojo.response.DeviceInfoResponse
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.OutputStream
import java.time.LocalDateTime
import java.util.*

@Service
class ThunderCountService {

    private val logger: Logger = LoggerFactory.getLogger(ThunderCountService::class.java)

    private val maxRetryTime = 5

    @Autowired
    private lateinit var thunderCountDao: ThunderCountDao

    @Autowired
    private lateinit var lastEndDateDao: LastEndDateDao

    @Autowired
    private lateinit var allDeviceDao: AllDeviceDao

    @Autowired
    private lateinit var config: Config

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TokenRefresh
    fun sync() {
        val currentDate = LocalDateTime.now()
        val endDate = LocalDateTime.of(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth,
                0, 0, 0)
        val startDate = endDate.minusDays(1)
        val lastEndDate = lastEndDateDao.findByJobMark(Constant.THUNDER_COUNT_JOB_MARKER)
        lastEndDate?.date?.let {
            if (it == endDate || it.isAfter(endDate)) {
                return
            }
        }
        val sns = allDeviceDao.findAll()
        sns.forEach{
            try {
                syncOne(it, startDate, endDate)
            } catch (e: Exception) {
                logger.warn("get thunder count error, sn is: {}", it)
                logger.error("the error: ", e)
            }
        }
        if (lastEndDate == null) {
            lastEndDateDao.save(LastEndDate(jobMark = Constant.THUNDER_COUNT_JOB_MARKER, date = endDate))
        } else {
            lastEndDateDao.update(Constant.THUNDER_COUNT_JOB_MARKER, endDate)
        }
    }

    fun syncOne(sn: String, startDate: LocalDateTime, endDate: LocalDateTime) {
        logger.info("sync thunder count, sn: {}, start: {}, end: {}", sn, startDate.toString(), endDate.toString())
        val url = String.format(Constant.DATE_INFO_URL_TEMPLATE, config.corporateId)
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            add("token", Config.token)
        }
        val request = DeviceInfoRequest(deviceSn = sn,
                beginDate = startDate.toInstant(Config.UTC_PLUS_8).toEpochMilli(),
                endDate = endDate.toInstant(Config.UTC_PLUS_8).toEpochMilli())
        val requestEntity = HttpEntity(request, headers)
        for (i in 0..maxRetryTime) {
            val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DeviceInfoResponse::class.java).body
            if (response != null && response.respCode == Config.successCode) {
                val historyList = arrayListOf<History>()
                response.t.forEach { historyList.add(History(it)) }
                var lighting1 = 0
                var lighting2 = 0
                var lighting3 = 0
                historyList.forEach {
                    lighting1 += it.switch1
                    lighting2 += it.switch2
                    lighting3 += it.switch3
                }
                val thunderCount = ThunderCount(sn = sn, date = startDate, lighting1 =  lighting1,
                                        lighting2 = lighting2, lighting3 = lighting3)
                thunderCountDao.saveOne(thunderCount)
                return
            }
            logger.error("get \"{}\" data failed! error message: {}", request.deviceSn,
                    objectMapper.writeValueAsString(response))
            logger.warn("get \"{}\" data failed! try again (remain retry time: ${maxRetryTime - i})",
                    request.deviceSn)
        }
    }

    fun getThunderCountData(sns: List<String>, startDate: LocalDateTime, endDate: LocalDateTime):
            Map<String, List<ThunderCount>?> {
        val result = mutableMapOf<String, List<ThunderCount>?>()
        sns.toHashSet().forEach {
            result[it] = thunderCountDao.findAllBySnAndDateBetweenOrderByDate(it, startDate, endDate)
        }
        return result
    }

    fun buildExcel(data: Map<String, List<ThunderCount>?>, start: LocalDateTime, end: LocalDateTime): HSSFWorkbook {
        val dateMap = TreeMap<String, Int>()
        var current = start
        var index = 0
        while (current.isBefore(end)) {
            index++
            dateMap[getDateString(current)] = index
            current = current.plusDays(1)
        }
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("数据")
        val titleRow = sheet.createRow(0)
        dateMap.forEach {
            titleRow.createCell(it.value).setCellValue(it.key)
        }
        data.forEach {
            buildDataRow(sheet, buildStringList(it.key, it.value, dateMap))
        }
        val descriptionSheet = workbook.createSheet("说明")
        val row1 = descriptionSheet.createRow(0)
        row1.createCell(0).setCellValue("数据格式：(A-雷击，B-雷击，C-雷击)")
        return workbook
    }

    fun buildDataRow(sheet: HSSFSheet, data: List<String?>) {
        val lastRow = sheet.lastRowNum;
        val row = sheet.createRow(lastRow + 1)
        for (i in data.indices) {
            val cellString = data[i]
            row.createCell(i).setCellValue(cellString ?: "(0, 0, 0)")
        }
    }

    fun getDateString(current: LocalDateTime): String {
        return "${current.year}-${current.monthValue}-${current.dayOfMonth}"
    }

    fun getIndex(thunderCount: ThunderCount, dateMap: Map<String, Int>): Int {
        val dateString = getDateString(thunderCount.date!!)
        return dateMap[dateString] ?: -1
    }

    fun buildCellContent(thunderCount: ThunderCount = ThunderCount()): String {
        return "(${thunderCount.lighting1}, ${thunderCount.lighting2}, ${thunderCount.lighting3})"
    }

    fun buildStringList(sn: String, thunderCounts: List<ThunderCount>?, dateMap: Map<String, Int>): List<String?> {
        if (dateMap.isEmpty()) {
            return listOf()
        }
        val dataArray = arrayOfNulls<String>(dateMap.size + 1)
        for (i in dataArray.indices) {
            dataArray[i] = buildCellContent()
        }
        thunderCounts?.forEach {
            val index = getIndex(it, dateMap)
            if (index > 0) {
                dataArray[index] = buildCellContent(it)
            }
        }
        dataArray[0] = sn
        return dataArray.toList()
    }

}