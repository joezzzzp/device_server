package com.zzz.device.pojo.persistent

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document


/**
@author zzz
@date 2019/4/28 10:39
 **/

@Document(collection = "user_info")
@JsonInclude
data class User(@Id var id: ObjectId? = null,
                @Indexed var account: String = "",
                var password: String = "",
                var nickName: String = "",
                @Indexed var mobile: String = "")