package com.like.common.view.badgeview

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt

class BadgeViewHelper(context: Context, target: View) {
    private val mBadgeView: BadgeView by lazy {
        BadgeView(context).apply {
            setTargetView(target)
        }
    }

    fun setMessageCount(messageCount: String, @ColorInt textColor: Int? = null, textSize: Int? = null, @ColorInt backgroundColor: Int? = null) {
        mBadgeView.setBadgeCount(messageCount)
        if (textColor != null) {
            mBadgeView.setBadgeTextColor(textColor)
        }
        if (textSize != null) {
            mBadgeView.setBadgeTextSize(textSize)
        }
        if (backgroundColor != null) {
            mBadgeView.setBadgeBackgroundColor(backgroundColor)
        }
    }

    fun getMessageCount(): String {
        return mBadgeView.text.toString()
    }
}