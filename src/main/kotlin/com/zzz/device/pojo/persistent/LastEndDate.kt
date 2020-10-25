package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "last_end_date")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class LastEndDate(@Id var id: ObjectId? = null,
                       @Indexed var jobMark: String? = null,
                       var date: LocalDateTime? = null)