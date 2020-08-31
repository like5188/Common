package com.like.common.base

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * 组件化架构时主程序使用，通过 [ModuleApplicationDelegate] 代理来管理组件 [IModuleApplication] 的生命周期。
 * 注意：
 * 1、[IModuleApplication]，是组件的 application 类必须实现的接口。组件的 application 类并没有真正继承 Application 类。
 * 只是功能类似而已，都用于一些功能的初始化。实际上此类只是实现 IModuleApplication 接口，然后 BaseComponentApplication
 * 类通过代理的方式来管理这些类的生命周期。
 * 2、组件实现了此接口后，还必须要有一个 public 的无参构造函数，用于反射构造组件 Application 的实例。
 * 3、必须在组件的 AndroidManifest.xml 文件中进行如下配置：<meta-data android:name="实现类的全限定类名" android:value="IModuleApplication" />
 */
open class BaseComponentApplication : BaseApplication() {
    /**
     * 组件中的Application的代理，用于组件的初始化
     */
    private val mModuleApplicationDelegate: ModuleApplicationDelegate by lazy { ModuleApplicationDelegate() }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        mModuleApplicationDelegate.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化组件的Application
        mModuleApplicationDelegate.onCreate(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        mModuleApplicationDelegate.onTerminate(this)
    }

    /**
     * 获取组件Application的实例
     */
    fun getModuleApplication(clazz: Class<out IModuleApplication>) =
            mModuleApplicationDelegate.getModuleApplication(clazz.name)

}

/**
 * 组件的 Application 中必须实现的接口，用于壳工程管理组件的生命周期。
 * 如果要获取 Application 的实例，可以使用：BaseApplication.sInstance 静态实例。
 *
 * 注意：
 * 实现类必须要有一个 public 的无参构造函数，用于反射构造组件 Application 的实例。
 * 必须在组件的 AndroidManifest.xml 文件中进行如下配置：
 * <meta-data
 * android:name="实现类的全限定类名"
 * android:value="IModuleApplication" />
 */
interface IModuleApplication {

    fun attachBaseContext(base: Context?)

    fun onCreate(application: Application)

    fun onTerminate(application: Application)

}

/**
 * 组件中的 Application 的代理类，用于管理所有组件的 Application 的生命周期。
 */
class ModuleApplicationDelegate : IModuleApplication {
    private val mModuleApplications = mutableMapOf<String, IModuleApplication>()

    override fun attachBaseContext(base: Context?) {
        base ?: return
        mModuleApplications.putAll(ManifestParser(base).parseMetaData())
        mModuleApplications.forEach {
            it.value.attachBaseContext(base)
        }
    }

    override fun onCreate(application: Application) {
        mModuleApplications.forEach {
            it.value.onCreate(application)
        }
    }

    override fun onTerminate(application: Application) {
        mModuleApplications.forEach {
            it.value.onTerminate(application)
        }
    }

    /**
     * 获取组件Application的实例。在ManifestParser中解析的时候实例化的。
     */
    internal fun getModuleApplication(className: String) = mModuleApplications[className]

}

/**
 * 从合并后的AndroidManifest.xml文件中解析出所有组件中实现了 IModuleApplication 接口的类
 */
class ManifestParser(private val mContext: Context) {
    companion object {
        private val TAG_MODULE_APPLICATION = IModuleApplication::class.java.simpleName
    }

    fun parseMetaData(): Map<String, IModuleApplication> {
        val moduleApplications = mutableMapOf<String, IModuleApplication>()
        val appInfo = try {
            mContext.packageManager.getApplicationInfo(mContext.packageName, PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            throw RuntimeException("组件中的AndroidManifest.xml下没有配置meta-data标签", e)
        }
        appInfo?.metaData?.apply {
            this.keySet().forEach {
                if (TAG_MODULE_APPLICATION == this.get(it)) {
                    parseModuleApplication(it)?.let { moduleApplication ->
                        moduleApplications[it] = moduleApplication
                    }
                }
            }
        }
        Log.d("ManifestParser", "解析出所有Module中的Application包含 $moduleApplications")
        return moduleApplications
    }

    private fun parseModuleApplication(className: String): IModuleApplication? {
        try {
            return Class.forName(className).newInstance() as IModuleApplication?
        } catch (e: Exception) {
            throw RuntimeException("实例化组件中实现${TAG_MODULE_APPLICATION}接口的Application失败", e)
        }
    }
}