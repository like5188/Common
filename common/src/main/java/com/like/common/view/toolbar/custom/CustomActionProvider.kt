package com.like.common.view.toolbar.custom

import android.content.Context
import android.view.View
import androidx.core.view.ActionProvider
import com.like.common.view.badgeview.BadgeViewManager

/**
 * 自定义视图提供者
 */
class CustomActionProvider(context: Context) : ActionProvider(context) {
    private val mToolbarCustomViewHelper: CustomViewManager by lazy {
        CustomViewManager(context)
    }
    private val mBadgeViewHelper: BadgeViewManager by lazy {
        BadgeViewManager(context, mToolbarCustomViewHelper.getContentView())
    }

    override fun onCreateActionView(): View {
        return mToolbarCustomViewHelper.getRootView()
    }

    /**
     * 获取自定义视图的帮助类，用于控制自定义视图
     */
    fun getToolbarCustomViewHelper() = mToolbarCustomViewHelper

    /**
     * 获取自定义视图中的消息视图帮助类，用于控制右上角的消息
     */
    fun getBadgeViewHelper() = mBadgeViewHelper
}