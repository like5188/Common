package com.like.common.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 界面状态控制器。
 *
 * @author like 2022-03-01
 * @param contentView           需要显示的内容视图
 */
class UiStatusController(private val contentView: View) {
    private val context = contentView.context
    private val root: FrameLayout by lazy {
        FrameLayout(contentView.context).apply {
            layoutParams = contentView.layoutParams
        }
    }
    private val statusMap = mutableMapOf<String, UiStatus>()

    fun addUiStatus(tag: String, status: UiStatus) {
        statusMap[tag] = status
    }

    fun removeUiStatus(tag: String) {
        statusMap.remove(tag)
    }

    fun clearUiStatus() {
        statusMap.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewDataBinding> getDataBinding(tag: String): T? {
        return statusMap[tag]?.dataBinding as? T
    }

    fun showContent() {
        hideUiStatus()
        if (contentView.visibility != View.VISIBLE)
            contentView.visibility = View.VISIBLE
    }

    fun hideContent() {
        if (contentView.visibility != View.GONE)
            contentView.visibility = View.GONE
    }

    fun showUiStatus(tag: String) {
        initRoot()
        hideContent()
        statusMap.forEach {
            if (it.key == tag) {
                it.value.show(context, root)
            } else {
                it.value.hide()
            }
        }
    }

    fun hideUiStatus() {
        statusMap.forEach {
            it.value.hide()
        }
    }

    /**
     * 把[root]添加到[contentView]和其 parent 的中间
     */
    private fun initRoot() {
        if (root.parent != null) return
        (contentView.parent as? ViewGroup)?.apply {
            removeView(contentView)
            addView(root)
            root.addView(contentView)
            contentView.visibility = View.GONE
        }
    }
}

/**
 * 界面状态
 */
class UiStatus(@LayoutRes private val layoutRes: Int) {
    internal var dataBinding: ViewDataBinding? = null
        private set

    internal fun show(context: Context, root: ViewGroup) {
        dataBinding = (dataBinding ?: getViewDataBinding(context, root, layoutRes)) ?: return
        if (dataBinding?.root?.visibility != View.VISIBLE)
            dataBinding?.root?.visibility = View.VISIBLE
    }

    internal fun hide() {
        if (dataBinding?.root?.visibility != View.GONE)
            dataBinding?.root?.visibility = View.GONE
    }

    private fun <T : ViewDataBinding> getViewDataBinding(context: Context, root: ViewGroup, @LayoutRes layoutResource: Int): T? {
        if (layoutResource == 0) return null
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResource, root, true)
    }

}