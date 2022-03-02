package com.like.common.sample.uistatuscontroller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityUiStatusControllerBinding
import com.like.common.sample.databinding.CommonViewUiStatusBinding
import com.like.common.util.CoilImageLoaderFactory
import com.tgf.kcwc.common.util.UiStatus
import com.tgf.kcwc.common.util.UiStatusController

class UiStatusControllerActivity : AppCompatActivity() {
    companion object {
        const val TAG_UI_STATUS_EMPTY = "tag_ui_status_empty"
        const val TAG_UI_STATUS_ERROR = "tag_ui_status_error"
        const val TAG_UI_STATUS_NETWORK_ERROR = "tag_ui_status_network_error"
        const val TAG_UI_STATUS_LOADING = "tag_ui_status_loading"
        const val TAG_UI_STATUS_NOT_FOUND_ERROR = "tag_ui_status_not_found_error"
    }

    private val mBinding: ActivityUiStatusControllerBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_ui_status_controller)
    }

    private val uiStatusController by lazy {
        UiStatusController(mBinding.tv).apply {
            addUiStatus(
                TAG_UI_STATUS_EMPTY,
                UiStatus<CommonViewUiStatusBinding>(this@UiStatusControllerActivity, R.layout.common_view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.icon_play_flag)
                    dataBinding.tvDes.text = "暂无数据~"
                })
            addUiStatus(
                TAG_UI_STATUS_ERROR,
                UiStatus<CommonViewUiStatusBinding>(this@UiStatusControllerActivity, R.layout.common_view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.icon_play_flag)
                    dataBinding.tvDes.text = "加载失败"
                    dataBinding.tvFun.text = "刷新试试"
                })
            addUiStatus(
                TAG_UI_STATUS_NETWORK_ERROR,
                UiStatus<CommonViewUiStatusBinding>(this@UiStatusControllerActivity, R.layout.common_view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.icon_play_flag)
                    dataBinding.tvDes.text = "你目前暂无网络"
                    dataBinding.tvFun.text = "刷新试试"
                })
            addUiStatus(
                TAG_UI_STATUS_LOADING,
                UiStatus<CommonViewUiStatusBinding>(this@UiStatusControllerActivity, R.layout.common_view_ui_status).apply {
                    dataBinding.iv.load(
                        R.drawable.gif_loading,
                        CoilImageLoaderFactory.createGifImageLoader(this@UiStatusControllerActivity)
                    )
                    dataBinding.tvDes.text = "正在奋力加载中..."
                })
            addUiStatus(
                TAG_UI_STATUS_NOT_FOUND_ERROR,
                UiStatus<CommonViewUiStatusBinding>(this@UiStatusControllerActivity, R.layout.common_view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.icon_play_flag)
                    dataBinding.tvTitle.text = "404错误页面"
                    dataBinding.tvDes.text = "Sorry您访问的页面不见了~"
                    dataBinding.tvFun.text = "刷新试试"
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun content(view: View) {
        uiStatusController.showContent()
    }

    fun empty(view: View) {
        uiStatusController.showUiStatus(TAG_UI_STATUS_EMPTY)
    }

    fun loading(view: View) {
        uiStatusController.showUiStatus(TAG_UI_STATUS_LOADING)
    }

    fun error(view: View) {
        uiStatusController.showUiStatus(TAG_UI_STATUS_ERROR)
    }

    fun notFoundError(view: View) {
        uiStatusController.showUiStatus(TAG_UI_STATUS_NOT_FOUND_ERROR)
    }

    fun networkError(view: View) {
        uiStatusController.showUiStatus(TAG_UI_STATUS_NETWORK_ERROR)
    }

}