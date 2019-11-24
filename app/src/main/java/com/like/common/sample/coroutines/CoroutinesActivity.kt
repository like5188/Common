package com.like.common.sample.coroutines

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityCoroutinesBinding
import kotlinx.coroutines.*

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
            println("1 ${Thread.currentThread().name}") // 在延迟后打印输出
            launch(Dispatchers.Main) {
                println("2 ${Thread.currentThread().name}") // 在延迟后打印输出
            }
            println("3 ${Thread.currentThread().name}") // 在延迟后打印输出
            delay(100)
            launch(Dispatchers.Main) {
                delay(100)
                println("4 ${Thread.currentThread().name}") // 在延迟后打印输出
            }
            println("5 ${Thread.currentThread().name}") // 在延迟后打印输出
        }
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
