package com.like.common.sample

import android.app.Application
import android.content.Context
import com.hjq.toast.ToastUtils
import com.like.common.util.ApplicationHolder
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// 这里可以在 Application 的 attachBaseContext 方法中添加初始化代码：MultiDex.install(this)，
// 或者直接继承 MultiDexApplication。
// 因为 Android 4.4 时会报错：Unable to get provider androidx.lifecycle.ProcessLifecycleOwnerInitializr
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
        }
        ApplicationHolder.onCreate(this)
        ToastUtils.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ApplicationHolder.onTerminate()
    }

}