package com.like.common.sample.coroutines

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityCoroutinesBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 协程测试
 */
class CoroutinesActivity : AppCompatActivity() {
    private val mBinding: ActivityCoroutinesBinding by lazy {
        DataBindingUtil.setContentView<ActivityCoroutinesBinding>(this, R.layout.activity_coroutines)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun test0(view: View) {
        GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L) // 无阻塞的等待1秒钟（默认时间单位是毫秒）
            println("World!") // 在延迟后打印输出
        }
        println("Hello,") // 主线程的协程将会继续等待
        Thread.sleep(2000L) // 阻塞主线程2秒钟来保证 JVM 存活
    }

    fun test1(view: View) {
        GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L)
            println("World!")
        }
        println("Hello,") // 主线程中的代码会立即执行
        runBlocking {
            // 但是这个函数阻塞了主线程。在主线程中调用了 runBlocking， 阻塞 会持续到 runBlocking 中的协程执行完毕。
            delay(2000L)  // ……我们延迟2秒来保证 JVM 的存活
        }
    }

    fun test2(view: View) {
    }

    fun test3(view: View) {
    }

    fun test4(view: View) {
    }
}
