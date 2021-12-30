package com.like.common.util

import android.database.Cursor
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

object Logger {
    private const val MAX_LOG_LINE_COUNT = 4000// 每行日志的最大长度限制，因为logcat在实现上每个message有4k字符长度限制
    private const val TAG = "Logger"

    /**
     * 开启打印日志。
     */
    fun open() {
        LogPrinter.isOpen = true
    }

    /**
     * 关闭打印日志。
     */
    fun close() {
        LogPrinter.isOpen = false
    }

    fun isOpen(): Boolean = LogPrinter.isOpen

    /**
     * 设置每行打印的字符数，用于分批打印，因为logcat在实现上每个message有4k字符长度限制

     * @param logLineCount 默认0，<=0表示不分批。最大不能超过[MAX_LOG_LINE_COUNT]
     */
    fun setLogLineCount(logLineCount: Int = 0) {
        LogPrinter.logLineCount = if (logLineCount > MAX_LOG_LINE_COUNT) MAX_LOG_LINE_COUNT else logLineCount
    }

    fun printXml(xml: String?, level: Int = Log.DEBUG) {
        printXml(getClassName(), xml, level)
    }

    fun printXml(tag: String, xml: String?, level: Int = Log.DEBUG) {
        LogPrinter.printXml(tag, xml, level)
    }

    fun printJson(json: String?, level: Int = Log.DEBUG) {
        printJson(getClassName(), json, level)
    }

    fun printJson(tag: String, json: String?, level: Int = Log.DEBUG) {
        LogPrinter.printJson(tag, json, level)
    }

    fun printCursor(cursor: Cursor?, level: Int = Log.DEBUG) {
        printCursor(getClassName(), cursor, level)
    }

    fun printCursor(tag: String, cursor: Cursor?, level: Int = Log.DEBUG) {
        LogPrinter.printCursor(tag, cursor, level)
    }

    fun <T> printCollection(collection: Collection<T>?, level: Int = Log.DEBUG) {
        printCollection(getClassName(), collection, level)
    }

    fun <T> printCollection(tag: String, collection: Collection<T>?, level: Int = Log.DEBUG) {
        LogPrinter.printCollection(tag, collection, level)
    }

    fun <T, V> printMap(map: Map<T, V>?, level: Int = Log.DEBUG) {
        printMap(getClassName(), map, level)
    }

    fun <T, V> printMap(tag: String, map: Map<T, V>?, level: Int = Log.DEBUG) {
        LogPrinter.printMap(tag, map, level)
    }

    fun v(obj: Any?) {
        v(getClassName(), obj)
    }

    fun v(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.VERBOSE)
    }

    fun d(obj: Any?) {
        d(getClassName(), obj)
    }

    fun d(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.DEBUG)
    }

    fun i(obj: Any?) {
        i(getClassName(), obj)
    }

    fun i(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.INFO)
    }

    fun w(obj: Any?) {
        w(getClassName(), obj)
    }

    fun w(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.WARN)
    }

    fun e(obj: Any?) {
        e(getClassName(), obj)
    }

    fun e(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.ERROR)
    }

    fun wtf(obj: Any?) {
        wtf(getClassName(), obj)
    }

    fun wtf(tag: String, obj: Any?) {
        LogPrinter.printBitch(tag, obj, Log.ASSERT)
    }

    /**
     * 获取调用Logger工具类的类的名字，如果没有，就返回"Logger"
     */
    private fun getClassName(): String {
        val stackTraces = Thread.currentThread().stackTrace
        for ((i, stackTrace) in stackTraces.withIndex()) {
            if (stackTrace.className.endsWith(TAG) && i + 1 < stackTraces.size) {
                val className = stackTraces[i + 1].className
                if (className.endsWith(TAG)) continue
                val startIndex = className.lastIndexOf(".")
                val endIndex = className.length
                return className.substring(startIndex + 1, endIndex)
            }
        }
        return TAG
    }

    private object LogPrinter {
        /**
         * 设置每行打印的字符数，用于分批打印，因为logcat在实现上每个message有4k字符长度限制
         * 默认0，<=0表示不分批。
         */
        var logLineCount: Int = 0// 每行打印的字符数，用于分批打印，因为logcat在实现上每个message有4k字符长度限制
        var isOpen = true// 是否打印日志。true：打印日志；false：不打印日志。

        fun printXml(tag: String, xml: String?, level: Int) {
            if (!isOpen) return
            if (xml.isNullOrEmpty()) {
                printBitch(tag, "Empty/Null xml content", Log.ERROR)
                return
            }

            try {
                val xmlInput = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                printBitch(tag, xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"), level)
            } catch (e: TransformerException) {
                printBitch(tag, "Invalid xml", Log.ERROR)
            }

        }

        fun printJson(tag: String, json: String?, level: Int) {
            if (!isOpen) return
            if (json.isNullOrEmpty()) {
                printBitch(tag, "Empty/Null json content", Log.ERROR)
                return
            }
            try {
                val jsonTrim = json.trim()
                if (jsonTrim.startsWith("{")) {
                    val jsonObject = JSONObject(jsonTrim)
                    val message = jsonObject.toString(2)
                    printBitch(tag, message, level)
                    return
                }
                if (jsonTrim.startsWith("[")) {
                    val jsonArray = JSONArray(jsonTrim)
                    val message = jsonArray.toString(2)
                    printBitch(tag, message, level)
                    return
                }
            } catch (e: JSONException) {
            }
            printBitch(tag, "Invalid Json", Log.ERROR)
        }

        fun printCursor(tag: String, cursor: Cursor?, level: Int) {
            if (!isOpen) return
            if (cursor == null) {
                printBitch(tag, "cursor为null", Log.ERROR)
                return
            }
            if (cursor.count <= 0) {
                printBitch(tag, "cursor count为0", Log.ERROR)
                return
            }
            val columnCount = cursor.columnCount
            for (i in 0 until columnCount) {
                printBitch(tag, cursor.getColumnName(i), level)
            }
            printBitch(tag, "\n", level)
            while (cursor.moveToNext()) {
                for (i in 0 until columnCount) {
                    printBitch(tag, cursor.getString(i), level)
                }
                printBitch(tag, "\n", level)
            }
        }

        fun <T> printCollection(tag: String, collection: Collection<T>?, level: Int) {
            if (!isOpen) return
            if (collection == null) {
                printBitch(tag, "collection为null", Log.ERROR)
                return
            }
            if (collection.isEmpty()) {
                printBitch(tag, "collection size为0", Log.ERROR)
                return
            }
            collection.forEach { printBitch(tag, it.toString(), level) }
        }

        fun <T, V> printMap(tag: String, map: Map<T, V>?, level: Int) {
            if (!isOpen) return
            if (map == null) {
                printBitch(tag, "map为null", Log.ERROR)
                return
            }
            if (map.isEmpty()) {
                printBitch(tag, "map size为0", Log.ERROR)
                return
            }
            for ((key, value) in map) {
                printBitch(tag, key.toString() + " : " + value, level)
            }
        }

        /**
         * 分批打印日志，避免日志打印不完全。因为logcat对每条日志长度有限制。

         * @param tag
         * @param obj
         * @param level
         */
        fun printBitch(tag: String, obj: Any?, level: Int) {
            if (!isOpen) return
            val text: String = obj?.toString() ?: "null"
            if (logLineCount <= 0) {
                print(tag, text, level)
            } else {
                val bitchCount = text.length / logLineCount + 1
                for (i in 0 until bitchCount) {
                    val start = i * logLineCount
                    val next = (i + 1) * logLineCount
                    val end = if (next > text.length) text.length else next
                    print(tag, "Logger bitch " + i + "：" + text.substring(start, end), level)
                }
            }
        }

        /**
         * 打印日志
         */
        private fun print(tag: String, message: String, level: Int) {
            when (level) {
                Log.VERBOSE -> Log.v(tag, message)
                Log.DEBUG -> Log.d(tag, message)
                Log.INFO -> Log.i(tag, message)
                Log.WARN -> Log.w(tag, message)
                Log.ERROR -> Log.e(tag, message)
                Log.ASSERT -> Log.wtf(tag, message)
            }
        }

    }
}
