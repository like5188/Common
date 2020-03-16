package com.like.common.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期工具类
 */
@SuppressLint("SimpleDateFormat")
object DateUtils {

    /**
     * 获取指定时间的年
     *
     * @param time  小于等于0表示当前时间
     */
    fun getYear(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.YEAR)
    }

    /**
     * 获取指定时间的月
     *
     * @param time  小于等于0表示当前时间
     */
    fun getMonth(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.MONTH) + 1
    }

    /**
     * 获取指定时间的日
     *
     * @param time  小于等于0表示当前时间
     */
    fun getDay(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取指定时间的小时(24小时制)
     *
     * @param time  小于等于0表示当前时间
     */
    fun getHour(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 获取指定时间的分钟
     *
     * @param time  小于等于0表示当前时间
     */
    fun getMinute(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.MINUTE)
    }

    /**
     * 获取指定时间的秒
     *
     * @param time  小于等于0表示当前时间
     */
    fun getSecond(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.SECOND)
    }

    /**
     * 获取指定时间的毫秒
     *
     * @param time  小于等于0表示当前时间
     */
    fun getMilliSecond(time: Long = 0): Int {
        val cal = Calendar.getInstance()
        if (time > 0) {
            cal.timeInMillis = time
        }
        return cal.get(Calendar.MILLISECOND)
    }

    /**
     * 获取星期几的中文字符
     *
     * @param time  小于等于0表示当前时间
     * @return
     */
    fun getWeekChinese(time: Long = 0): String = with(Calendar.getInstance()) {
        if (time > 0) {
            timeInMillis = time
        }
        when (get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "星期日"
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            else -> ""
        }
    }

    /**
     * 获取指定日期和当前日期相差的年数
     */
    fun getDiffYears(time: Long): Int {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = time
        val year = cal.get(Calendar.YEAR)
        return year - curYear
    }

    /**
     * 获取指定日期和当前日期相差的月数
     */
    fun getDiffMonths(time: Long): Int {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        val curMonth = cal.get(Calendar.MONTH)
        cal.timeInMillis = time
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        return (year - curYear) * 12 + (month - curMonth)
    }

    /**
     * 获取指定日期和当前日期相差的天数
     */
    fun getDiffDays(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60 * 60 * 24)
    }

    /**
     * 获取指定日期和当前日期相差的小时数
     */
    fun getDiffHours(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60 * 60)
    }

    /**
     * 获取指定日期和当前日期相差的分钟数
     */
    fun getDiffMinutes(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60)
    }

    /**
     * 获取指定日期和当前日期相差的分钟数
     */
    fun getDiffSeconds(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / 1000L
    }

    /**
     * 获取指定日期和当前日期相差的分钟数
     */
    fun getDiffMilliSeconds(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff
    }

    /**
     * 比较两个日期的大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return:0：date1==date2或者出错；1：date1>date2；-1：date1<date2；
     */
    fun compareDate(date1: String, date2: String, format: String): Int {
        val df = SimpleDateFormat(format)
        try {
            val dt1 = df.parse(date1)
            val dt2 = df.parse(date2)
            return when {
                dt1.time > dt2.time -> 1
                dt1.time < dt2.time -> -1
                else -> 0
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return 0
    }

    /**
     * 判断是否为闰年
     */
    fun isLeapYear(year: Int): Boolean =
            (year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0)

    /**
     * 指定某年中的某月的第一天是星期几
     *
     * @param year
     * @param month
     * @return
     */
    fun getWeekdayOfMonth(year: Int, month: Int): Int =
            Calendar.getInstance().let {
                it.set(year, month - 1, 1)
                it.get(Calendar.DAY_OF_WEEK) - 1
            }

    /**
     * 获取某一天属于某个月的第几个星期
     *
     * @return
     */
    fun getWeekOfMonth(year: Int, mouth: Int, date: Int): Int =
            Calendar.getInstance().let {
                it.set(year, mouth - 1, date)
                it.get(Calendar.WEEK_OF_MONTH)
            }

    /**
     * 获取指定年、月的天数
     */
    fun getDaysOfMonth(year: Int, month: Int): Int =
            Calendar.getInstance().let {
                it.set(Calendar.YEAR, year)
                it.set(Calendar.MONTH, month - 1)
                it.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

    /**
     * 获取指定格式的日期字符串。
     *
     * @param date "2018-09-09"
     * @param srcFormat 传入date的格式
     * @param desFormat 返回的格式
     */
    fun format(date: String, srcFormat: String, desFormat: String): String =
            try {
                format(SimpleDateFormat(srcFormat).parse(date), desFormat)
            } catch (e: Exception) {
                ""
            }

    /**
     * 获取指定格式的日期字符串。
     *
     * @param time
     * @param format 返回的格式
     */
    fun format(time: Long, format: String): String = format(Date(time), format)

    /**
     * 获取指定格式的日期字符串。
     *
     * @param date
     * @param format 返回的格式
     */
    fun format(date: Date, format: String): String = SimpleDateFormat(format).format(date)

}
