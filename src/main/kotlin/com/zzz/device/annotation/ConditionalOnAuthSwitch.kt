package com.zzz.device.annotation

import com.zzz.device.config.AuthCondition
import org.springframework.context.annotation.Conditional


/**
@author zzz
@date 2019/2/20 18:18
 **/

@Conditional(AuthCondition::class)
annotation class ConditionalOnAuthSwitch (val needAuth: Boolean = true)