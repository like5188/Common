package com.like.common.view.update.shower

import android.annotation.SuppressLint
import android.view.View
import com.like.common.R
import com.like.common.base.BaseDialogFragment
import com.like.common.databinding.DialogFragmentDownloadProgressBinding
import com.like.common.util.AppUtils
import com.like.common.util.toDataStorageUnit
import com.like.common.view.update.TAG_CONTINUE
import com.like.common.view.update.TAG_PAUSE
import com.like.livedatabus.LiveDataBus
import com.like.retrofit.util.getCustomNetworkMessage
import kotlin.math.roundToInt

/**
 * 强制更新使用对话框显示进度条
 */
class ForceUpdateDialogShower(private val fragmentManager: androidx.fragment.app.FragmentManager) : Shower {
    private val downloadProgressDialog = DefaultDownloadProgressDialog()

    override fun onDownloadPending() {
        downloadProgressDialog.show(fragmentManager)
        downloadProgressDialog.setTitle("正在连接服务器...")
        downloadProgressDialog.setMessage("")// 避免中途网络断开，然后重新连接后点击继续时，错误信息还是存在
    }

    override fun onDownloadRunning(currentSize: Long, totalSize: Long) {
        downloadProgressDialog.setTitle("下载中，请稍后...")
        downloadProgressDialog.setProgress(currentSize, totalSize)
    }

    override fun onDownloadPaused(currentSize: Long, totalSize: Long) {
        downloadProgressDialog.setTitle("已经暂停下载")
        downloadProgressDialog.setProgress(currentSize, totalSize)
    }

    override fun onDownloadSuccessful(totalSize: Long) {
        downloadProgressDialog.dismissAllowingStateLoss()
        AppUtils.getInstance(downloadProgressDialog.context).exitApp()
    }

    override fun onDownloadFailed(throwable: Throwable?) {
        downloadProgressDialog.setTitle("下载失败！")
        downloadProgressDialog.setMessage(throwable.getCustomNetworkMessage())
    }

    class DefaultDownloadProgressDialog : BaseDialogFragment<DialogFragmentDownloadProgressBinding>() {

        override fun getLayoutResId(): Int {
            return R.layout.dialog_fragment_download_progress
        }

        override fun initView(binding: DialogFragmentDownloadProgressBinding) {
            binding.btnPause.setOnClickListener {
                LiveDataBus.post(TAG_PAUSE)
            }
            binding.btnContinue.setOnClickListener {
                LiveDataBus.post(TAG_CONTINUE)
            }
            binding.ivClose.setOnClickListener {
                LiveDataBus.post(TAG_PAUSE)
                dismissAllowingStateLoss()
                AppUtils.getInstance(context).exitApp()
            }
            isCancelable = false
        }

        @SuppressLint("SetTextI18n")
        fun setProgress(currentSize: Long, totalSize: Long) {
            getBinding()?.apply {
                val progress = (currentSize.toFloat() / totalSize.toFloat() * 100).roundToInt()
                pbProgress.progress = progress
                tvPercent.text = "$progress%"
                tvSize.text = "${currentSize.toDataStorageUnit()}/${totalSize.toDataStorageUnit()}"
            }
        }

        fun setTitle(title: String) {
            getBinding()?.apply {
                tvTitle.text = title
            }
        }

        fun setMessage(msg: String) {
            getBinding()?.apply {
                tvMessage.visibility = if (msg.isEmpty()) View.GONE else View.VISIBLE
                tvMessage.text = msg
            }
        }

    }
}