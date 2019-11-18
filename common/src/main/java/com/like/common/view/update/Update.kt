package com.like.common.view.update

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.like.common.util.PermissionUtils
import com.like.common.view.update.controller.DownloadController
import com.like.common.view.update.shower.Shower
import java.io.File

class Update {
    private val mContext: Context?
    private val mDownloadController: DownloadController
    private val mPermissionUtils: PermissionUtils

    constructor(fragmentActivity: FragmentActivity) {
        mContext = fragmentActivity.applicationContext
        mDownloadController = DownloadController(fragmentActivity)
        mPermissionUtils = PermissionUtils(fragmentActivity)
    }

    constructor(fragment: Fragment) {
        mContext = fragment.context?.applicationContext ?: throw IllegalArgumentException("can not get context from fragment")
        mDownloadController = DownloadController(fragment)
        mPermissionUtils = PermissionUtils(fragment)
    }

    /**
     *
     * @param shower        显示者
     */
    fun setShower(shower: Shower) {
        mDownloadController.mShowerDelegate.shower = shower
    }

    /**
     *
     * @param url           下载地址。必须设置。可以是完整路径或者子路径
     * @param versionName   下载的文件的版本号。可以不设置。用于区分下载的文件的版本。如果url中包括了版本号，可以不传。
     */
    fun setUrl(url: String, versionName: String = "") {
        require(url.isNotEmpty()) { "url can not be empty" }
        val context = mContext ?: throw IllegalArgumentException("can not get context from fragment")
        val downloadFile = createDownloadFile(context, url, versionName) ?: throw IllegalArgumentException("wrong download url")
        mDownloadController.mUrl = url
        mDownloadController.mDownloadFile = downloadFile
    }

    /**
     *
     * @param downloader    下载工具类。必须设置
     */
    fun setDownloader(downloader: IDownloader) {
        mDownloadController.mDownloader = downloader
    }

    @SuppressLint("MissingPermission")
    fun download() {
        mPermissionUtils.checkStoragePermissions({
            if (!it) return@checkStoragePermissions
            mDownloadController.cont()
        })
    }

    fun cancel() {
        mDownloadController.cancel()
    }

    private fun createDownloadFile(context: Context, url: String, versionName: String): File? = try {
        val downloadFileName = if (versionName.isNotEmpty()) {
            val lastPointPosition = url.lastIndexOf(".")// 最后一个"."的位置
            val fileName = url.substring(url.lastIndexOf("/") + 1, lastPointPosition)// 从url获取的文件名，不包括后缀。"xxx"
            val fileSuffix = url.substring(lastPointPosition)// 后缀。".apk"
            "$fileName-$versionName$fileSuffix"
        } else {
            url.substring(url.lastIndexOf("/") + 1)// 从url获取的文件名，包括后缀。"xxx.xxx"
        }
        File(context.cacheDir, downloadFileName)
    } catch (e: Exception) {
        null
    }
}