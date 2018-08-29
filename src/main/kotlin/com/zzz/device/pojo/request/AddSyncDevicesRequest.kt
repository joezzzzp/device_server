package com.zzz.device.pojo.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class AddSyncDevicesRequest(var snList: List<String> = listOf())