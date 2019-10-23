package com.like.common.view.update.shower

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.like.common.R
import com.like.common.base.BaseDialogFragment
import com.like.common.databinding.DialogFragmentDownloadProgressBinding
import com.like.common.util.AppUtils
import com.like.common.util.toDataStorageUnit
import com.like.common.view.update.TAG_CONTINUE
import com.like.common.view.update.TAG_PAUSE
import com.like.livedatabus.LiveDataBus
import com.like.retrofit.util.getCustomNetworkMessage

/**
 * 强制更新使用对话框显示进度条
 */
class ForceUpdateDialogShower(private val fragmentManager: androidx.fragment.app.FragmentManager) : Shower {
    private val downloadProgressDialog = DefaultDownloadProgressDialog()

    override fun onDownloadPending() {
        downloadProgressDialog.show(fragmentManager)
        downloadProgressDialog.setCancelableOnClickViewOrBackKey(false)
        downloadProgressDialog.setAnim(R.style.dialogFragment_anim_bottom_in_bottom_out)
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

    class DefaultDownloadProgressDialog : BaseDialogFragment() {
        private var mBinding: DialogFragmentDownloadProgressBinding? = null

        override fun getViewDataBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, args: Bundle?): ViewDataBinding? {
            mBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.dialog_fragment_download_progress,
                    null, false
            )
            mBinding?.also {
                it.btnPause.setOnClickListener {
                    LiveDataBus.post(TAG_PAUSE)
                }
                it.btnContinue.setOnClickListener {
                    LiveDataBus.post(TAG_CONTINUE)
                }
                it.ivClose.setOnClickListener {
                    LiveDataBus.post(TAG_PAUSE)
                    dismissAllowingStateLoss()
                    AppUtils.getInstance(context).exitApp()
                }
            }
            return mBinding
        }

        @SuppressLint("SetTextI18n")
        fun setProgress(currentSize: Long, totalSize: Long) {
            mBinding?.apply {
                val progress = Math.round(currentSize.toFloat() / totalSize.toFloat() * 100)
                pbProgress.progress = progress
                tvPercent.text = "$progress%"
                tvSize.text = "${currentSize.toDataStorageUnit()}/${totalSize.toDataStorageUnit()}"
            }
        }

        fun setTitle(title: String) {
            mBinding?.apply {
                tvTitle.text = title
            }
        }

        fun setMessage(msg: String) {
            mBinding?.apply {
                tvMessage.visibility = if (msg.isEmpty()) View.GONE else View.VISIBLE
                tvMessage.text = msg
            }
        }

    }
}