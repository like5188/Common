package com.like.common.view.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.ViewCustomToolbarMenuBinding
import com.like.common.util.onPreDrawListener
import com.like.common.view.BadgeView

/**
 * 自定义的 Toolbar 菜单视图。
 * 此视图包括图标、文本、消息数三个元素。
 */
class CustomToolbarMenu(context: Context) : ICustomToolbarMenu {
    private val mBinding: ViewCustomToolbarMenuBinding by lazy {
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_custom_toolbar_menu,
            null, false
        )
    }
    private val mBadgeView by lazy {
        BadgeView(context).apply {
            setTargetView(mBinding.cl)
        }
    }

    init {
        // 必须设置一遍，才会有layoutParams属性
        setMargin(0, 0, 0, 0)
    }

    override fun getView(): View = mBinding.root

    /**
     * 设置自定义视图的内容的 padding
     *
     * 自定义视图的root为第一层，那么真正的内容在第二层显示，
     * 这里其实是设置第二层的margin，用于配合[com.like.common.view.badgeview.BadgeView]来显示消息并调整其位置
     */
    override fun setContentPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mBinding.cl.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
            .apply {
                leftMargin = left
                topMargin = top
                rightMargin = right
                bottomMargin = bottom
            }
    }

    /**
     * 设置自定义视图的 margin
     */
    override fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
        mBinding.root.onPreDrawListener {
            mBinding.root.layoutParams = when (mBinding.root.layoutParams) {
                is Toolbar.LayoutParams -> {// 如果是Toolbar中的NavigationView：Toolbar.LayoutParams
                    Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT)
                }
                is ActionMenuView.LayoutParams -> {// 如果是Toolbar中的Menu：ActionMenuView.LayoutParams
                    ActionMenuView.LayoutParams(ActionMenuView.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.MATCH_PARENT)
                }
                is LinearLayout.LayoutParams -> {// 如果是Titlebar中的Default
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                }
                is RelativeLayout.LayoutParams -> {
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                }
                is FrameLayout.LayoutParams -> {
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
                }
                is ConstraintLayout.LayoutParams -> {
                    ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                }
                else -> {
                    MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT)
                }
            }.apply {
                leftMargin = left
                topMargin = top
                rightMargin = right
                bottomMargin = bottom
            }
        }
    }

    /**
     * 设置自定义视图点击监听
     *
     * @param clickListener     点击监听。默认为null，表示取消监听。
     */
    override fun setOnClickListener(clickListener: View.OnClickListener?) {
        mBinding.root.setOnClickListener(clickListener)
    }

    /**
     * 设置自定义视图的文本
     *
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     */
    override fun setText(title: String, @ColorInt textColor: Int?, textSize: Float?) {
        if (title.isEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
            mBinding.tvTitle.text = ""
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = title
            if (textColor != null) {
                mBinding.tvTitle.setTextColor(textColor)
            }
            if (textSize != null) {
                mBinding.tvTitle.textSize = textSize
            }
        }
    }

    override fun getText(): String {
        return mBinding.tvTitle.text.toString()
    }

    /**
     * 设置自定义视图的图标
     *
     * @param iconResId         图标资源id。如果设置为0，表示去掉图标。
     */
    override fun setIcon(@DrawableRes iconResId: Int) {
        if (iconResId == 0) {
            mBinding.iv.visibility = View.GONE
            mBinding.iv.setImageDrawable(null)
        } else {
            mBinding.iv.visibility = View.VISIBLE
            mBinding.iv.setImageResource(iconResId)
        }
    }

    override fun getBadgeView(): BadgeView = mBadgeView

}
