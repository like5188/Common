package com.like.common.view.dragview.view.util

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.like.common.util.StoragePrivateUtils

/**
 * 使用com.danikula:videocache:2.7.1来缓存VideoView适配时需要的代理类工厂
 */
object HttpProxyCacheServerFactory {
    //全局初始化一个本地代理服务器
    private var proxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer {
        val p = proxy
        if (p != null) {
            return p
        }

        val created = HttpProxyCacheServer.Builder(context)
                .maxCacheFilesCount(20)
                .cacheDirectory(StoragePrivateUtils.InternalStorageHelper.getCacheDir(context))
                .build()
        proxy = created
        return created
    }

}