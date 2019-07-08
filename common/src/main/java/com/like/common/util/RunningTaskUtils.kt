package com.like.common.util

import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import java.lang.reflect.Field
import java.util.*

class RunningTaskUtils(private val context: Context) {
    companion object {
        private val TAG = RunningTaskUtils::class.java.simpleName
        private const val TWENTY_SECOND = 1000 * 20
        private const val THIRTY_SECOND = 1000 * 60 * 60 * 3
    }

    private val activityManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    private val mUsageStatsManager: UsageStatsManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        } else {
            null
        }
    }

    fun getTopRunningTasks(): ComponentName? {
        //用两次取当前应用的办法来提高正确性
        return getTopRunningTasks(true)
    }

    fun getTopRunningTasks(isFirst: Boolean): ComponentName? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return getTopActivtyBelow21()
        }
        var runningTopActivity: ComponentName? = null
        var topPackageName: String? = null
        val time = System.currentTimeMillis()
        // We get usage stats for the last 10 seconds
        val stats = if (isFirst) {
            mUsageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TWENTY_SECOND, time)
        } else {
            mUsageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - THIRTY_SECOND, time)
        }
        //            LogUtil.e(TAG,"isFirst="+isFirst+",queryUsageStats cost:"+ (System.currentTimeMillis()-start));
        // Sort the stats by the last time used
        if (stats != null) {
            val mySortedMap = TreeMap<Long, UsageStats>()
            //                start=System.currentTimeMillis();
            for (usageStats in stats) {
                mySortedMap[usageStats.lastTimeUsed] = usageStats
            }
            //                LogUtil.e(TAG,"isFirst="+isFirst+",mySortedMap cost:"+ (System.currentTimeMillis()-start));
            if (mySortedMap.isNotEmpty()) {
                var mLastEventField: Field? = null
                val keySet = mySortedMap.navigableKeySet()
                val iterator = keySet.descendingIterator()
                while (iterator.hasNext()) {
                    val usageStats = mySortedMap[iterator.next()]
                    if (mLastEventField == null) {
                        try {
                            mLastEventField = UsageStats::class.java.getField("mLastEvent")
                        } catch (e: NoSuchFieldException) {
                            break
                        }
                    }
                    if (mLastEventField != null) {
                        var lastEvent = 0
                        try {
                            lastEvent = mLastEventField.getInt(usageStats)
                        } catch (e: IllegalAccessException) {
                            break
                        }

                        if (lastEvent == 1) {
                            topPackageName = usageStats?.packageName
                            break
                        }
                    } else {
                        break
                    }
                }
                if (topPackageName == null) {
                    topPackageName = mySortedMap[mySortedMap.lastKey()]?.packageName
                    if ("com.android.systemui" == topPackageName) {
                        var currentKey: Long? = null
                        var tempPackage = topPackageName
                        currentKey = mySortedMap.floorKey(mySortedMap.lastKey() - 1)
                        if (currentKey != null) {
                            tempPackage = mySortedMap[currentKey]?.packageName
                        }
                        if (tempPackage != null) {
                            if (context.packageName == tempPackage) {
                                currentKey = mySortedMap.floorKey(currentKey!! - 1)
                                if (currentKey != null) {
                                    tempPackage = mySortedMap[currentKey]?.packageName
                                }
                            }
                        }
                        if (tempPackage != null) {
                            topPackageName = tempPackage
                        }
                    }
                }
                runningTopActivity = ComponentName(topPackageName, "")
                Log.d(TAG, topPackageName)
            } else {
                Log.d(TAG, "mySortedMap.isEmpty")
                runningTopActivity = if (isFirst) {
                    getTopRunningTasks(false)
                } else {
                    getTopActivtyBelow21()
                }
            }
        }
        if (runningTopActivity?.packageName == context.packageName) {
            runningTopActivity = getTopActivtyBelow21()
        }
        return runningTopActivity
    }

    fun getTopActivtyBelow21(): ComponentName? = activityManager?.getRunningTasks(1)?.get(0)?.topActivity

    fun getTopRunningTasksOrigin(): ComponentName? {
        var runningTopActivity: ComponentName? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val time = System.currentTimeMillis()
            // We get usage stats for the last 10 seconds
            //            long start=System.currentTimeMillis();
            mUsageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TWENTY_SECOND, time)?.let { stats ->
                // Sort the stats by the last time used
                val mySortedMap = TreeMap<Long, UsageStats>()
                stats.forEach { usageStats ->
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap.isNotEmpty()) {
                    mySortedMap[mySortedMap.lastKey()]?.packageName?.let { topPackageName ->
                        runningTopActivity = ComponentName(topPackageName, "")
                        Log.d(TAG, topPackageName)
                    }
                } else {
                    Log.d(TAG, "mySortedMap.isEmpty")
                    runningTopActivity = getTopActivtyBelow21()
                }
            }
            if (runningTopActivity?.packageName == context.packageName) {
                runningTopActivity = getTopActivtyBelow21()
            }
        } else {
            runningTopActivity = getTopActivtyBelow21()
        }
        return runningTopActivity
    }

    fun needToSet(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val time = System.currentTimeMillis()
        // We get usage stats for the last 10 seconds
        val stats = mUsageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time)
        stats?.isEmpty() ?: false
    } else {
        false
    }
}