package com.zzz.device.controller

import com.zzz.device.dao.UserDao
import com.zzz.device.pojo.persistent.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*


/**
@author zzz
@date 2019/4/28 15:06
 **/

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    private lateinit var userDao: UserDao

    @GetMapping("/{id}")
    fun findUserById(@PathVariable id: String): Map<String, Any?> {
        val user = userDao.findById(id)
        return mapOf("code" to 200, "message" to "success", "data" to user)
    }

    @GetMapping("/query")
    fun findUserByAccountOrMobile(@RequestParam account: String, @RequestParam mobile: String): Map<String, Any?> {
        if (StringUtils.isEmpty(account) && StringUtils.isEmpty(mobile)) {
            return mapOf("code" to 403, "message" to "invalid params", "data" to null)
        }
        var user: User? = null
        if (!StringUtils.isEmpty(mobile)) {
            user = userDao.findByMobile(mobile)
        }
        if (!StringUtils.isEmpty(account)) {
            user = userDao.findByAccount(account)
        }
        return mapOf("code" to 200, "message" to "success", "data" to user)
    }

    @PostMapping("/register")
    fun register(@RequestBody user: User): Map<String, Any?> {
        userDao.save(user)
        return mapOf("code" to 200, "message" to "success", "data" to null)
    }

    @GetMapping("/updatePassword")
    fun updatePassword(@RequestParam account: String, @RequestParam newPassword: String): Map<String, Any?> {
        userDao.updatePasswordByAccount(account, newPassword)
        return mapOf("code" to 200, "message" to "success", "data" to null)
    }
}