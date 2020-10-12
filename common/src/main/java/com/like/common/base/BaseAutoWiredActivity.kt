package com.like.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.like.common.util.AutoWired
import com.like.common.util.injectForIntentExtras

/**
 * 通过反射从[android.content.Intent]中获取参数值，并赋值给被[AutoWired]注解的字段。
 */
open class BaseAutoWiredActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectForIntentExtras()
    }

}