package com.like.common.util

import android.content.res.Resources
import android.support.design.widget.TabLayout
import android.util.TypedValue
import android.widget.LinearLayout
import java.lang.reflect.Field

object TabLayoutUtils {
    /**
     * 设置tablayout的indicator的左右边距
     */
    fun setIndicator(tabs: TabLayout, leftMargin: Int, rightMargin: Int) {
        val tabLayout = tabs.javaClass
        var tabStrip: Field? = null
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        tabStrip!!.isAccessible = true
        var llTab: LinearLayout? = null
        try {
            llTab = tabStrip.get(tabs) as LinearLayout?
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftMargin.toFloat(), Resources.getSystem().displayMetrics).toInt()
        val right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightMargin.toFloat(), Resources.getSystem().displayMetrics).toInt()

        for (i in 0 until llTab!!.childCount) {
            val child = llTab.getChildAt(i)
            child.setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            params.leftMargin = left
            params.rightMargin = right
            child.layoutParams = params
            child.invalidate()
        }
    }
}
