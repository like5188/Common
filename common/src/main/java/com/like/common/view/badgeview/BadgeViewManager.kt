package com.like.common.view.badgeview

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt

/**
 * [BadgeView]管理类
 */
class BadgeViewManager(context: Context, target: View) {
    private val mBadgeView: BadgeView by lazy {
        BadgeView(context).apply {
            setTargetView(target)
        }
    }

    /**
     * 设置消息数
     *
     * @param messageCount      消息数
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小，sp。默认为null，表示不设置，保持原样。
     * @param backgroundColor   背景颜色。默认为null，表示不设置，保持原样。
     */
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

    /**
     * 获取显示的消息数
     */
    fun getMessageCount(): String {
        return mBadgeView.text.toString()
    }
}