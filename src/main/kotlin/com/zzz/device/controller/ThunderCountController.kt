package com.zzz.device.controller

import com.zzz.device.service.ThunderCountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/thunderCount")
class ThunderCountController {

    @Autowired
    private lateinit var thunderCountService: ThunderCountService

    @PostMapping("/sync")
    fun startSync() {
        thunderCountService.sync()
    }

    @PostMapping("/getThunderCount")
    fun getThunderCount(@RequestParam start: String,
                        @RequestParam end: String) {

    }
}