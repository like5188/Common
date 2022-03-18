package com.like.common.view.toolbar

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.like.common.view.BadgeView

interface ICustomToolbarMenu {
    fun getView(): View

    /**
     * 设置自定义视图的内容的 padding
     *
     * 自定义视图的root为第一层，那么真正的内容在第二层显示，
     * 这里其实是设置第二层的margin，用于配合[com.like.common.view.BadgeView]来显示消息并调整其位置
     */
    fun setContentPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0)

    /**
     * 设置自定义视图的 margin
     */
    fun setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0)

    /**
     * 设置自定义视图点击监听
     *
     * @param clickListener     点击监听。默认为null，表示取消监听。
     */
    fun setOnClickListener(clickListener: View.OnClickListener? = null)

    /**
     * 设置自定义视图的文本
     *
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     */
    fun setText(title: String, @ColorInt textColor: Int? = null, textSize: Float? = null)

    fun getText(): String

    /**
     * 设置自定义视图的图标
     *
     * @param iconResId         图标资源id。如果设置为0，表示不显示图标。
     */
    fun setIcon(@DrawableRes iconResId: Int)

    fun getBadgeView(): BadgeView

}
