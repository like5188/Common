package com.like.common.view.toolbar.custom

import android.content.Context
import android.view.View
import androidx.core.view.ActionProvider

/**
 * 自定义视图提供者
 */
class CustomActionProvider(context: Context) : ActionProvider(context) {
    private val mCustomViewManager: CustomViewManager by lazy {
        CustomViewManager(context)
    }

    override fun onCreateActionView(): View {
        return mCustomViewManager.getView()
    }

    /**
     * 获取自定义视图的帮管理类，用于控制自定义视图
     */
    fun getCustomViewManager() = mCustomViewManager

}