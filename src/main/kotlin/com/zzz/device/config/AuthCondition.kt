package com.zzz.device.config

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.env.get
import org.springframework.core.type.AnnotatedTypeMetadata


/**
@author zzz
@date 2019/2/20 17:59
 **/

class AuthCondition: Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val envValue: String?
        try {
            envValue = context.environment["auth.switchOn"]
        } catch (e:IllegalStateException) {
            return false
        }
        val switchOn = "true".equals(envValue, true)
        val properties = metadata.getAnnotationAttributes("com.zzz.device.annotation.ConditionalOnAuthSwitch")
        val needAuth = properties?.get("needAuth") ?: false
        return switchOn == needAuth
    }
}