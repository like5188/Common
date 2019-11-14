package com.like.common.sample.update

import android.app.Application
import com.like.common.view.update.IDownloader
import com.like.retrofit.RetrofitUtil
import com.like.retrofit.download.livedata.DownloadLiveData
import com.like.retrofit.utils.RequestConfig
import java.io.File

class RetrofitDownloader(application: Application) : IDownloader {
    init {
        RetrofitUtil.getInstance().apply {
            this.initDownload(RequestConfig.Builder().application(application).build())
        }
    }

    override suspend fun download(url: String, downloadFile: File, threadCount: Int, deleteCache: Boolean, callbackInterval: Long): DownloadLiveData {
        return RetrofitUtil.getInstance().download(url, downloadFile, threadCount, deleteCache, callbackInterval)
    }

}