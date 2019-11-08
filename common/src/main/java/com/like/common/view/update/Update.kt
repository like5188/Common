package com.like.common.view.update

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.RequiresPermission
import com.like.common.util.SingletonHolder
import com.like.common.view.update.controller.DownloadController
import com.like.common.view.update.shower.Shower
import com.like.common.view.update.shower.ShowerDelegate
import com.like.retrofit.RetrofitUtil
import java.io.File
import kotlin.jvm.functions.FunctionN

@SuppressLint("MissingPermission")
class Update {
    private var mShowerDelegate: ShowerDelegate = ShowerDelegate()
    private lateinit var mDownloadController: DownloadController
    // 必须初始化才能使用
    private lateinit var mDownloader: IDownloader
    private lateinit var mUrl: String
    // 可以不用初始化
    private var mVersionName: String = ""

    companion object : SingletonHolder<Update>(object : FunctionN<Update> {
        override val arity: Int = 0 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): Update {
            return Update()
        }
    }) {
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun with(activity: androidx.fragment.app.FragmentActivity): Update {
            mContext = activity.applicationContext
            return getInstance()
        }

        fun with(fragment: androidx.fragment.app.Fragment): Update {
            mContext = fragment.activity?.applicationContext
            return getInstance()
        }
    }

    /**
     * 设置显示者
     */
    fun shower(shower: Shower): Update {
        mShowerDelegate.shower = shower
        return this
    }

    /**
     * @param downloader 下载工具类。必须设置
     */
    fun setDownloader(downloader: IDownloader): Update {
        mDownloader = downloader
        return this
    }

    /**
     * 下载地址。必须设置。可以是完整路径或者子路径
     */
    fun url(url: String): Update {
        mUrl = url
        return this
    }

    /**
     * 下载的文件的版本号。可以不设置
     *
     * 用于区分下载的文件的版本。如果url中包括了版本号，可以不传。
     */
    fun versionName(versionName: String): Update {
        mVersionName = versionName
        return this
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun download() {
        if (mContext == null) throw UnsupportedOperationException("mContext must be initialize before calling download()")
        if (!::mDownloader.isInitialized) throw UnsupportedOperationException("mDownloadRetrofitUtils must be initialize before calling download()")
        if (!::mUrl.isInitialized) throw UnsupportedOperationException("mUrl must be initialize before calling download()")
        if (mUrl.isEmpty()) throw IllegalArgumentException("mUrl must not be empty")
        val downloadFile = createDownloadFile(mUrl, mVersionName)
                ?: throw IllegalArgumentException("wrong download mUrl")
        if (!::mDownloadController.isInitialized)
            mDownloadController = DownloadController(mContext!!, mDownloader, mUrl, downloadFile, mShowerDelegate)
        mDownloadController.cont()
    }

    fun cancel() {
        if (::mDownloadController.isInitialized) {
            mDownloadController.cancel()
        }
    }

    private fun createDownloadFile(url: String, versionName: String): File? = try {
        val downloadFileName = if (versionName.isNotEmpty()) {
            val lastPointPosition = url.lastIndexOf(".")// 最后一个"."的位置
            val fileName = url.substring(url.lastIndexOf("/") + 1, lastPointPosition)// 从url获取的文件名，不包括后缀。"xxx"
            val fileSuffix = url.substring(lastPointPosition)// 后缀。".apk"
            "$fileName-$versionName$fileSuffix"
        } else {
            url.substring(url.lastIndexOf("/") + 1)// 从url获取的文件名，包括后缀。"xxx.xxx"
        }
        File(mContext?.cacheDir, downloadFileName)
    } catch (e: Exception) {
        null
    }
}