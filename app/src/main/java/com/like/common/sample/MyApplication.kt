package com.like.common.sample

import android.content.Context
import androidx.multidex.MultiDex
import com.like.common.base.BaseApplication

// 这里可以在 Application 的 attachBaseContext 方法中添加初始化代码：MultiDex.install(this)，
// 或者直接继承 MultiDexApplication。
// 因为 Android 4.4 时会报错：Unable to get provider androidx.lifecycle.ProcessLifecycleOwnerInitializr
class MyApplication : BaseApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    
}