package com.zzz.device.pojo.persistent

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


/**
@author zzz
@date 2019/3/15 16:23
 **/

@Document(collection = "api_count")
data class ApiCount(@Id var id: ObjectId? = null,
                    @Indexed var key: String = "",
                    var count: Int = 0,
                    @Indexed var saveTime: LocalDateTime? = null)