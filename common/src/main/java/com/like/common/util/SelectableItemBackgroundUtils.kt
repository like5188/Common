package com.like.common.util

import android.content.Context
import android.content.res.TypedArray
import com.like.common.R

object SelectableItemBackgroundUtils {
    /**
     * 获取有边界的触摸反馈动画资源id
     */
    fun getSelectableItemBackgroundResourceId(context: Context): Int {
        val attrs = intArrayOf(R.attr.selectableItemBackground)
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs)
        val resourceId = typedArray.getResourceId(0, 0)
        typedArray.recycle()
        return resourceId
    }

    /**
     * 获取无边界的触摸反馈动画资源id（要求API21以上）
     */
    fun getSelectableItemBackgroundBorderlessResourceId(context: Context): Int {
        val attrs = intArrayOf(R.attr.selectableItemBackgroundBorderless)
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs)
        val resourceId = typedArray.getResourceId(0, 0)
        typedArray.recycle()
        return resourceId
    }
}