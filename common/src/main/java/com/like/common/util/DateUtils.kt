package com.like.common.util

import android.annotation.SuppressLint
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期工具类
 */
@SuppressLint("SimpleDateFormat")
object DateUtils {

    /**
     * 获取当前日期时间星期
     *
     * @return "yyyy-MM-dd HH:mm:ss 星期几"
     */
    val currentDateTimeWeek: String
        get() {
            val sb = StringBuilder()
            val date = Date()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            sb.append(sdf.format(date))
            val c = Calendar.getInstance()
            c.timeZone = TimeZone.getTimeZone("GMT+8:00")
            val week = c.get(Calendar.DAY_OF_WEEK).toString()
            when (week) {
                "1" -> sb.append(" 星期日")
                "2" -> sb.append(" 星期一")
                "3" -> sb.append(" 星期二")
                "4" -> sb.append(" 星期三")
                "5" -> sb.append(" 星期四")
                "6" -> sb.append(" 星期五")
                "7" -> sb.append(" 星期六")
            }
            return sb.toString()
        }

    /**
     * 获取当前日期
     *
     * @return
     */
    val currentDate: Date = Date()

    /**
     * 获取当前时间
     *
     * @return
     */
    val currentTime: Time = Time(System.currentTimeMillis())

    /**
     * 获取当前年份
     *
     * @return
     */
    val curYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    /**
     * 获取当前月份
     *
     * @return
     */
    val curMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    /**
     * 获取当前天
     *
     * @return
     */
    val curDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    /**
     * 获取当月的天数
     */
    val daysOfCurrentMonth: Int = getDaysByYearMonth(curYear, curMonth)

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

    /**
     * 获取星期几
     *
     * @param time
     * @return
     */
    fun getWeek(time: Long): String =
            Calendar.getInstance().let {
                it.timeInMillis = time
                when (it.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "周日"
                    Calendar.MONDAY -> "周一"
                    Calendar.TUESDAY -> "周二"
                    Calendar.WEDNESDAY -> "周三"
                    Calendar.THURSDAY -> "周四"
                    Calendar.FRIDAY -> "周五"
                    Calendar.SATURDAY -> "周六"
                    else -> ""
                }
            }

    /**
     * 把时间转换成今天、昨天、前天、指定格式
     *
     * @param time
     * @return
     */
    fun parseDate2Custom(time: Long, format: String): String {
        var result = ""
        try {
            val now = Calendar.getInstance()
            val oneDayMillis = (24 * 60 * 60 * 1000).toLong()
            val todayPassMillis = (1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND))).toLong()//毫秒数
            val nowMillis = now.timeInMillis
            result = when {
                nowMillis - time < todayPassMillis -> "今天"
                nowMillis - time < todayPassMillis + oneDayMillis -> "昨天"
                nowMillis - time < todayPassMillis + oneDayMillis * 2 -> "前天"
                else -> SimpleDateFormat(format).format(time)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 比较两个日期的大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return
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
     * 是否今天
     *
     * @param date
     * @return
     */
    fun isToday(date: Date): Boolean =
            Calendar.getInstance().let {
                it.time = date
                it.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                        it.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            }

    /**
     * 是否今年
     *
     * @param date
     * @return
     */
    fun isThisYear(date: Date): Boolean =
            Calendar.getInstance().let {
                it.time = date
                it.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
            }

    /**
     * 一年前就返回相隔的年数
     *
     * @param date
     * @return
     */
    fun isPreYear(date: Date): Int =
            Calendar.getInstance().let {
                it.time = date
                Calendar.getInstance().get(Calendar.YEAR) - it.get(Calendar.YEAR)
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
     * 获取某一天属于某个月的第几个星期的第几天
     *
     * @return 1代表星期天...7代表星期六
     */
    fun getDayOfMonth(year: Int, mouth: Int, date: Int): Int =
            Calendar.getInstance().let {
                it.set(year, mouth - 1, date)
                it.get(Calendar.DAY_OF_WEEK)
            }

    /**
     * 获取指定年、月的某月份的天数
     */
    fun getDaysByYearMonth(year: Int, month: Int): Int =
            Calendar.getInstance().let {
                it.set(Calendar.YEAR, year)
                it.set(Calendar.MONTH, month - 1)
                it.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

}
