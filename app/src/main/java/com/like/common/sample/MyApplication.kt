package com.like.common.sample

import androidx.multidex.MultiDexApplication

// 这里必须用 MultiDexApplication，因为 Android 19 时会报错：Unable to get provider androidx.lifecycle.ProcessLifecycleOwnerInitializr
class MyApplication : MultiDexApplication() {
}