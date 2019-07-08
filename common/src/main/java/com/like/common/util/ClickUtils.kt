package com.like.common.util

import android.util.Log
import android.view.View

/**
 * 点击事件相关的工具类
 */

object ClickUtils {
    /**
     * 设置点击监听，连击一定次数后触发clickListener
     *
     * @param clickTimes    点击次数
     * @param view
     * @param clickListener
     */
    fun addOnClickListener(clickTimes: Int, view: View, clickListener: View.OnClickListener) {
        addOnClickListener(500, clickTimes, view, clickListener)
    }

    /**
     * 设置点击监听，连击一定次数后触发clickListener
     *
     * @param interval      两次点击的时间间隔
     * @param clickTimes    点击次数
     * @param view
     * @param clickListener
     */
    fun addOnClickListener(interval: Long, clickTimes: Int, view: View, clickListener: View.OnClickListener) {
        view.setOnClickListener(object : View.OnClickListener {
            var firstTime: Long = 0
            var count: Int = 0

            override fun onClick(v: View) {
                val secondTime = System.currentTimeMillis()
                // 判断每次点击的事件间隔是否符合连击的有效范围
                // 不符合时，有可能是连击的开始，否则就仅仅是单击
                if (secondTime - firstTime <= interval) {
                    count++
                } else {
                    count = 1
                }
                // 延迟，用于判断用户的点击操作是否结束
                firstTime = secondTime
                if (count <= clickTimes) {
                    Log.i("ClickUtils", "连续点击次数：$count")
                }
                if (count == clickTimes) {
                    clickListener.onClick(view)
                }
            }
        })
    }

    /**
     * 防抖动按钮点击（500毫秒内只触发一次点击事件）
     *
     * @param view
     * @param clickListener
     */
    fun addOnClickListener(view: View, clickListener: View.OnClickListener) {
        RxJavaUtils.addOnClickListener(500, view, clickListener)
    }

    /**
     * 防抖动按钮点击（指定时间间隔内只触发一次点击事件）
     *
     * @param interval      时间间隔(毫秒)
     * @param view
     * @param clickListener
     */
    fun addOnClickListener(interval: Long, view: View, clickListener: View.OnClickListener) {
        RxJavaUtils.addOnClickListener(interval, view, clickListener)
    }
}