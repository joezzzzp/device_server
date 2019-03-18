package com.zzz.device.pojo.request

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


/**
@author zzz
@date 2019/3/18 10:24
 **/

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ApiCountRequest(var key: String? = null,
                           var beginTime: LocalDateTime? = null,
                           var endTime: LocalDateTime? = null)