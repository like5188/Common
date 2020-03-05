package com.like.common.view.dragview.view.util

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer

object HttpProxyCacheServerFactory {
    //全局初始化一个本地代理服务器
    private var proxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer? {
        if (proxy == null) {
            proxy = HttpProxyCacheServer(context)
        }
        return proxy
    }

}