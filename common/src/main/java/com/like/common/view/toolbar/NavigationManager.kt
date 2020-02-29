package com.like.common.view.toolbar

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import com.like.common.databinding.ToolbarBinding
import com.like.common.view.toolbar.custom.CustomViewManager

/**
 * [Toolbar]中的导航按钮管理类
 */
class NavigationManager(private val mContext: Context, private val mBinding: ToolbarBinding) {
    // 自定义视图的管理类，用于控制自定义视图
    private val mCustomViewManager: CustomViewManager by lazy {
        CustomViewManager(mContext, mBinding.navigationView)
    }

    /**
     * 显示原生导航按钮
     *
     * @param resid             图标资源id。如果设置为0，表示去掉图标。
     * @param listener          点击监听
     */
    fun showView(@DrawableRes resid: Int = 0, listener: View.OnClickListener? = null) {
        if (resid == 0) {
            mBinding.toolbar.navigationIcon = null
        } else {
            mBinding.toolbar.setNavigationIcon(resid)
            mBinding.toolbar.setNavigationOnClickListener(listener)
        }
    }

    /**
     * 显示自定义视图的导航按钮
     *
     * @param resid             图标资源id。如果设置为0，表示去掉图标。
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     * @param listener          点击监听
     */
    fun showCustomView(
            @DrawableRes resid: Int = 0,
            title: String = "",
            @ColorInt textColor: Int? = null,
            textSize: Float? = null,
            listener: View.OnClickListener? = null
    ) {
        mCustomViewManager.setIcon(resid)
        mCustomViewManager.setTitle(title, textColor, textSize)
        mCustomViewManager.setOnClickListener(listener)
    }

    /**
     * 设置自定义视图的导航按钮的margin
     */
    fun setCustomViewMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mCustomViewManager.setMargin(left, top, right, bottom)
    }

    /**
     * 设置自定义视图的导航按钮的内容的padding，用于调整消息位置
     */
    fun setCustomViewContentPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mCustomViewManager.setContentPadding(left, top, right, bottom)
    }

    /**
     * 显示自定义视图的导航按钮的消息
     *
     * @param messageCount      消息数
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小，sp。默认为null，表示不设置，保持原样。
     * @param backgroundColor   背景颜色。默认为null，表示不设置，保持原样。
     */
    fun showCustomViewMessageCount(
            messageCount: String,
            @ColorInt textColor: Int? = null,
            textSize: Int? = null,
            @ColorInt backgroundColor: Int? = null
    ) {
        mCustomViewManager.setMessageCount(messageCount, textColor, textSize, backgroundColor)
    }
}