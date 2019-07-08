package com.like.common.view.update.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.RequiresPermission
import com.like.common.util.AppUtils
import com.like.common.view.update.TAG_CONTINUE
import com.like.common.view.update.TAG_PAUSE
import com.like.common.view.update.TAG_PAUSE_OR_CONTINUE
import com.like.common.view.update.shower.ShowerDelegate
import com.like.livedatabus.liveDataBusRegister
import com.like.livedatabus.liveDataBusUnRegister
import com.like.livedatabus_annotations.BusObserver
import com.like.retrofit.RetrofitUtils
import com.like.retrofit.entity.DownloadInfo
import com.like.retrofit.livedata.BaseCallLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 下载控制类。通过[com.like.livedatabus.LiveDataBus]来控制。
 *
 * Manifest.permission.INTERNET
 * Manifest.permission.WRITE_EXTERNAL_STORAGE
 */
@SuppressLint("MissingPermission")
class DownloadController(
        private val context: Context,
        private val mRetrofitUtils: RetrofitUtils,
        private val mUrl: String,
        private val downloadFile: File,
        private val mShowerDelegate: ShowerDelegate
) {
    private var mCallLiveData: BaseCallLiveData<DownloadInfo>? = null

    init {
        liveDataBusRegister()
    }

    fun cancel() {
        mCallLiveData?.cancel()
        mCallLiveData = null
        liveDataBusUnRegister(TAG_PAUSE_OR_CONTINUE)
        liveDataBusUnRegister(TAG_PAUSE)
        liveDataBusUnRegister(TAG_CONTINUE)
    }

    /**
     * 如果是暂停，就继续下载；如果是下载中，就暂停
     */
    @BusObserver([TAG_PAUSE_OR_CONTINUE])
    fun pauseOrContinue() {
        if (mCallLiveData != null) {// 正在下载
            pause()
        } else {
            cont()
        }
    }

    @BusObserver([TAG_PAUSE])
    fun pause() {
        mCallLiveData?.pause()
        mCallLiveData = null
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @BusObserver([TAG_CONTINUE])
    fun cont() {
        if (mCallLiveData != null) return// 正在下载

        mShowerDelegate.onDownloadPending()

        // 下载
        GlobalScope.launch {
            mCallLiveData = mRetrofitUtils.download(mUrl, downloadFile, 3).await()
            launch(Dispatchers.Main) {
                mCallLiveData?.observeForever { downloadInfo ->
                    when (downloadInfo?.status) {
                        DownloadInfo.Status.STATUS_RUNNING -> {
                            mShowerDelegate.onDownloadRunning(downloadInfo.cachedSize, downloadInfo.totalSize)
                        }
                        DownloadInfo.Status.STATUS_PAUSED -> {
                            mShowerDelegate.onDownloadPaused(downloadInfo.cachedSize, downloadInfo.totalSize)
                        }
                        DownloadInfo.Status.STATUS_SUCCESSFUL -> {
                            AppUtils.getInstance(context).install(downloadFile)
                            mCallLiveData = null
                            mShowerDelegate.onDownloadSuccessful(downloadInfo.totalSize)
                        }
                        DownloadInfo.Status.STATUS_FAILED -> {
                            mShowerDelegate.onDownloadFailed(downloadInfo.throwable)
                            mCallLiveData = null// 用于点击继续重试
                        }
                    }
                }
            }
        }
    }

}