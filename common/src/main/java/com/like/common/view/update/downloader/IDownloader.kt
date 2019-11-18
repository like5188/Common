package com.like.common.view.update.downloader

import com.like.retrofit.download.livedata.DownloadLiveData
import java.io.File

interface IDownloader {
    suspend fun download(
            url: String,
            downloadFile: File,
            threadCount: Int = 1,
            deleteCache: Boolean = false,
            callbackInterval: Long = 200L
    ): DownloadLiveData
}