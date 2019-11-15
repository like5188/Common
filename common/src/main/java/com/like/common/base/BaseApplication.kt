package com.like.common.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

open class BaseApplication : Application() {
    companion object {
        lateinit var sInstance: BaseApplication
    }

    private val activities = Collections.synchronizedList(mutableListOf<Activity>())
    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
            activities.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            activities.add(activity)
        }

    }

    override fun onCreate() {
        super.onCreate()
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

    fun getAllActivities() = activities

    /**
     * 关闭所有Activity
     */
    fun finishAllActivities() {
        activities.forEach { it.finish() }
    }

    /**
     * 关闭所有Activity，除了指定的Activity。
     */
    fun <T : Activity> finishAllActivitiesExclude(vararg classes: Class<T>) {
        activities.filter { !classes.contains(it::class.java) }.forEach { it.finish() }
    }

    /**
     * 关闭指定的所有Activity
     */
    fun <T : Activity> finishAllActivitiesInclude(vararg classes: Class<T>) {
        activities.filter { classes.contains(it::class.java) }.forEach { it.finish() }
    }

}