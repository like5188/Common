package com.like.common.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ActionProvider
import androidx.databinding.DataBindingUtil
import com.like.common.databinding.TitlebarCustomButtonBinding
import com.like.common.view.titlebar.TitlebarCustomButtonManager

/**
 * Menu菜单的自定义视图提供者
 */
class CustomActionProvider(context: Context) : ActionProvider(context) {
    private val mCustomViewManager: TitlebarCustomButtonManager by lazy {
        val toolbarCustomViewBinding = DataBindingUtil.inflate<TitlebarCustomButtonBinding>(
                LayoutInflater.from(context),
                R.layout.titlebar_custom_button,
                null, false
        )
        TitlebarCustomButtonManager(context, toolbarCustomViewBinding)
    }

    override fun onCreateActionView(): View {
        return mCustomViewManager.getView()
    }

    /**
     * 获取自定义视图的帮管理类，用于控制自定义视图
     */
    fun getCustomViewManager() = mCustomViewManager

}