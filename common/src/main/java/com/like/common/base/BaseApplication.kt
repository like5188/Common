package com.like.common.base

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Bundle
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.*

/**
 * 集成了 [koin] 依赖注入框架，对 [Activity] 进行了管理
 */
open class BaseApplication : Application() {
    companion object {
        lateinit var sInstance: BaseApplication
    }

    private val activities = Collections.synchronizedList(mutableListOf<Activity>())
    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            activities.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activities.add(activity)
        }

    }

    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // Android context
            androidContext(this@BaseApplication)
        }
        sInstance = this
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    /**
     * 退出整个app
     */
    fun exitApp() {
        // 关闭所有界面，避免后一句代码执行后，如果栈中还有界面，会重启进程并启动下一个界面，导致不能彻底退出应用。
        finishAllActivities()
        System.exit(0)
    }

    /**
     * 关闭所有Activity
     */
    fun finishAllActivities() {
        activities.forEach { it.finish() }
    }

    /**
     * 关闭所有Activity，除了指定的Activity。
     */
    fun <T : Activity> finishAllActivitiesExclude(vararg classes: Class<out T>) {
        activities.filter { !classes.contains(it::class.java) }.forEach { it.finish() }
    }

    /**
     * 关闭指定的所有Activity
     */
    fun <T : Activity> finishAllActivitiesInclude(vararg classes: Class<out T>) {
        activities.filter { classes.contains(it::class.java) }.forEach { it.finish() }
    }

    /**
     * 获取指定Activity实例
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Activity> getActivity(clazz: Class<T>): T? {
        return activities.firstOrNull {
            it.javaClass == clazz
        } as? T
    }

    fun getAllActivities(): List<Activity> = activities

    /**
     * 这里参考https://www.cnblogs.com/zhujiabin/p/6874508.html
     */
    fun isDebug(): Boolean {
        return applicationInfo != null && (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}