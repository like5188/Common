package com.like.common.util

import android.content.Context
import android.os.Vibrator
import androidx.annotation.RequiresPermission

/**
 * 震动相关
 */
object VibrateUtils {
    /**
     * 震动
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(context: Context, milliseconds: Long) {
        // 获取系统震动服务
        val vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vib.hasVibrator()) {
            vib.vibrate(milliseconds)
        }
    }
}