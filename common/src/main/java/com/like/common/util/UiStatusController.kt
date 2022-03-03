package com.like.common.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/*
    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/swipeRefreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!--SwipeRefreshLayout 中的子视图在使用 UiStatusController 进行状态管理时，必须在 xml 文件中多加一层，否则状态不会显示-->
        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <TextView
                android:id="@+id/tv"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center"
                android:text="内容"
                android:textSize="30sp" />
        </FrameLayout>
    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
 */

/**
 * 界面状态控制器。
 * 注意：SwipeRefreshLayout 中的子视图在使用 UiStatusController 进行状态管理时，
 * 必须在 xml 文件中多加一层，否则状态不会正常显示。（见顶部注释）
 *
 * @author like 2022-03-02
 * @param contentView           需要显示的内容视图
 */
open class UiStatusController(private val contentView: View) {
    private val root: FrameLayout by lazy {
        FrameLayout(contentView.context).apply {
            layoutParams = contentView.layoutParams
        }
    }
    private val statusMap = mutableMapOf<String, UiStatus<out ViewDataBinding>>()

    fun addUiStatus(tag: String, status: UiStatus<out ViewDataBinding>) {
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

    open fun showUiStatus(tag: String) {
        initRoot()
        hideContent()
        statusMap.forEach {
            if (it.key == tag) {
                it.value.show(root)
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
class UiStatus<T : ViewDataBinding>(context: Context, @LayoutRes private val layoutRes: Int) {
    val dataBinding: T by lazy {
        DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, null, false)
    }

    internal fun show(root: ViewGroup) {
        if (dataBinding.root.parent == null) {
            root.addView(dataBinding.root)
        }
        if (dataBinding.root.visibility != View.VISIBLE)
            dataBinding.root.visibility = View.VISIBLE
    }

    internal fun hide() {
        if (dataBinding.root.visibility != View.GONE)
            dataBinding.root.visibility = View.GONE
    }

}