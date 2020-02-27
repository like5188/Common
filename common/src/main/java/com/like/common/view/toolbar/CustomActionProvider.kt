package com.like.common.view.toolbar

import android.content.Context
import android.view.View
import androidx.core.view.ActionProvider
import com.like.common.view.badgeview.BadgeViewHelper

class CustomActionProvider(context: Context) : ActionProvider(context) {
    private val mToolbarCustomViewHelper: ToolbarCustomViewHelper by lazy {
        ToolbarCustomViewHelper(context)
    }
    private val mBadgeViewHelper: BadgeViewHelper by lazy {
        BadgeViewHelper(context, mToolbarCustomViewHelper.getContentView())
    }

    override fun onCreateActionView(): View {
        return mToolbarCustomViewHelper.getRootView()
    }

    fun getToolbarCustomViewHelper() = mToolbarCustomViewHelper

    fun getBadgeViewHelper() = mBadgeViewHelper
}