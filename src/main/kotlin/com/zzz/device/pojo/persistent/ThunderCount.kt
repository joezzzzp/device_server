package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "thunder_count")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ThunderCount(@Id var id: ObjectId? = null,
                        var sn: String? = null,
                        var date: LocalDateTime? = null,
                        var lighting1: Int = 0,
                        var lighting2: Int = 0,
                        var lighting3: Int = 0)