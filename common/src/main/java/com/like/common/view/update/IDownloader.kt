package com.like.common.view.update

import com.like.retrofit.common.livedata.PauseCancelLiveData
import com.like.retrofit.download.DownloadInfo
import java.io.File

interface IDownloader {
    suspend fun download(
            url: String,
            downloadFile: File,
            threadCount: Int = 1,
            deleteCache: Boolean = false,
            callbackInterval: Long = 200L
    ): PauseCancelLiveData<DownloadInfo>
}