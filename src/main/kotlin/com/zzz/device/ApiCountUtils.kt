package com.zzz.device

import java.util.concurrent.atomic.AtomicInteger


/**
@author zzz
@date 2019/3/15 16:34
 **/

class ApiCountUtils {

    companion object {

        const val INFO_KEY = "info"
        const val HISTORY_KEY = "history"
        const val UPDATE_TIME_KEY = "update_time"

        private val apiCountMap = mutableMapOf<String, AtomicInteger>().apply {
            this[INFO_KEY] = AtomicInteger(0)
            this[HISTORY_KEY] = AtomicInteger(0)
            this[UPDATE_TIME_KEY] = AtomicInteger(0)
        }

        fun count(key: String) {
            apiCountMap[key]?.incrementAndGet()
        }

        fun collect(): List<Pair<String, Int>> {
            val ret = mutableListOf<Pair<String, Int>>()
            apiCountMap.forEach { key, value ->
                ret.add(Pair(key, value.get()))
                value.set(0)
            }
            return ret
        }
    }

}