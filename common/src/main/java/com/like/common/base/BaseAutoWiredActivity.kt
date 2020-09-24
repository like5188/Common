package com.like.common.base

import android.os.Bundle
import com.like.common.util.AutoWired
import com.like.common.util.injectForIntentExtras

/**
 * 通过反射从[android.content.Intent]中获取参数值，并赋值给被[AutoWired]注解的字段。
 */
open class BaseAutoWiredActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectForIntentExtras()
    }

}