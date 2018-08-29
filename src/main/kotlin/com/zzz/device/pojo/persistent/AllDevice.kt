package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "all_devices")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class AllDevice(@Id var id: ObjectId? = null,
                     @Indexed var sn: String = "")