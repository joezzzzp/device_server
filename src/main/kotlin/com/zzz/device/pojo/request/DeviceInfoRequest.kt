package com.zzz.device.pojo.request

data class DeviceInfoRequest(val deviceSn: String = "",
                             var beginDate: Long = 0L,
                             val endDate: Long = 0L)
