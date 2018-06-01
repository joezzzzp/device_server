package com.zzz.device.pojo.response

data class DeviceInfoResponse(val respCode: String = "",
                              val respMessage: String = "",
                              val t: ArrayList<DateItem> = arrayListOf())

data class DateItem(val collectDate: Long = 0L,
                    val deviceNum: String = "",
                    val items: ArrayList<PropertyItem> = arrayListOf())

data class PropertyItem(val decimalDigit: String = "",
                        val displayName: String = "",
                        val fieldId: String = "",
                        val fieldType: String = "",
                        val fieldUnit: String = "",
                        val hexValues: String = "",
                        val numValues: Float = -1f)