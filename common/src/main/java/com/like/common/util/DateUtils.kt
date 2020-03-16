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
     * @param time  默认为当前时间
     */
    fun getYear(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.YEAR)
    }

    /**
     * 获取指定时间的月
     *
     * @param time  默认为当前时间
     */
    fun getMonth(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.MONTH) + 1
    }

    /**
     * 获取指定时间的日
     *
     * @param time  默认为当前时间
     */
    fun getDay(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取指定时间的小时(24小时制)
     *
     * @param time  默认为当前时间
     */
    fun getHour(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 获取指定时间的分钟
     *
     * @param time  默认为当前时间
     */
    fun getMinute(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.MINUTE)
    }

    /**
     * 获取指定时间的秒
     *
     * @param time  默认为当前时间
     */
    fun getSecond(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.SECOND)
    }

    /**
     * 获取指定时间的毫秒
     *
     * @param time  默认为当前时间
     */
    fun getMilliSecond(time: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return cal.get(Calendar.MILLISECOND)
    }

    /**
     * 获取指定时间是星期几
     *
     * @param time  默认为当前时间
     * @return 中文字符串
     */
    fun getWeekChinese(time: Long = System.currentTimeMillis()): String = with(Calendar.getInstance()) {
        timeInMillis = time
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
     * 获取指定时间和当前时间相差的年数
     */
    fun getDiffYears(time: Long): Int {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        cal.timeInMillis = time
        val year = cal.get(Calendar.YEAR)
        return year - curYear
    }

    /**
     * 获取指定时间和当前时间相差的月数
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
     * 获取指定时间和当前时间相差的天数
     */
    fun getDiffDays(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60 * 60 * 24)
    }

    /**
     * 获取指定时间和当前时间相差的小时数
     */
    fun getDiffHours(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60 * 60)
    }

    /**
     * 获取指定时间和当前时间相差的分钟数
     */
    fun getDiffMinutes(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / (1000L * 60)
    }

    /**
     * 获取指定时间和当前时间相差的分钟数
     */
    fun getDiffSeconds(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff / 1000L
    }

    /**
     * 获取指定时间和当前时间相差的分钟数
     */
    fun getDiffMilliSeconds(time: Long): Long {
        val diff = System.currentTimeMillis() - time
        return diff
    }

    /**
     * 比较两个字符串日期的大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return:0：date1==date2或者格式化出错；1：date1>date2；-1：date1<date2；
     */
    fun compareDateString(date1: String, date2: String, format: String): Int {
        val df = SimpleDateFormat(format)
        try {
            val dt1 = df.parse(date1) ?: return 0
            val dt2 = df.parse(date2) ?: return 0
            return compareDate(dt1, dt2)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return 0
    }

    /**
     * 比较两个日期的大小
     *
     * @param date1
     * @param date2     默认为当前日期
     * @param format
     * @return:0：date1==date2；1：date1>date2；-1：date1<date2；
     */
    fun compareDate(date1: Date, date2: Date = Date()): Int = when {
        date1.time > date2.time -> 1
        date1.time < date2.time -> -1
        else -> 0
    }

    /**
     * 判断是否为闰年
     *
     * @param year  默认为当年
     */
    fun isLeapYear(year: Int = getYear()): Boolean = (year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0)

    /**
     * 指定某年中的某月的第一天是星期几
     *
     * @param year      默认为当年
     * @param month     默认为当月
     */
    fun getWeekdayOfMonth(year: Int = getYear(), month: Int = getMonth()): Int = Calendar.getInstance().let {
        it.set(year, month - 1, 1)
        it.get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * 获取某一天属于某个月的第几个星期
     *
     * @param year      默认为当年
     * @param month     默认为当月
     * @param day       默认为当日
     *
     */
    fun getWeekOfMonth(year: Int = getYear(), month: Int = getMonth(), day: Int = getDay()): Int = Calendar.getInstance().let {
        it.set(year, month - 1, day)
        it.get(Calendar.WEEK_OF_MONTH)
    }

    /**
     * 获取指定年、月的天数
     *
     * @param year      默认为当年
     * @param month     默认为当月
     */
    fun getDaysOfMonth(year: Int = getYear(), month: Int = getMonth()): Int = Calendar.getInstance().let {
        it.set(Calendar.YEAR, year)
        it.set(Calendar.MONTH, month - 1)
        it.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 日期字符串转日期
     *
     * @param date      "2018-09-09"
     * @param format    传入日期字符串的格式
     */
    fun format(date: String, format: String): Date? =
            try {
                SimpleDateFormat(format).parse(date)
            } catch (e: Exception) {
                null
            }

    /**
     * 时间转日期字符串。
     *
     * @param time      时间
     * @param format    返回的格式
     */
    fun format(time: Long, format: String): String = format(Date(time), format)

    /**
     * 日期转日期字符串。
     *
     * @param date      日期
     * @param format    返回的格式
     */
    fun format(date: Date, format: String): String = SimpleDateFormat(format).format(date)

}
