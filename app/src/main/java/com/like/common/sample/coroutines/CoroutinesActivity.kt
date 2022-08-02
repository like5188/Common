package com.like.common.sample.coroutines

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityCoroutinesBinding
import com.like.common.util.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.system.measureTimeMillis

/**
 * 协程测试
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityCoroutinesBinding>(this, R.layout.activity_coroutines)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    private suspend fun a(): Int {
        delay(1000)
        throw IllegalArgumentException("error a")
        return 1
    }

    private suspend fun b(): Int {
        delay(2000)
//        throw IllegalArgumentException("error b")
        return 2
    }

    fun successIfOneSuccess(view: View) {
        lifecycleScope.launch {
            val cost = measureTimeMillis {
                supervisorScope {
                    flowOf(::a.asFlow(), ::b.asFlow()).flattenMerge().toList().forEach {
                        Logger.v(it)
                    }
                }

            }
            Logger.d(cost)
        }
    }

    fun scan(view: View) {
        lifecycleScope.launch {
            flowOf(1, 2, 3).scan(0) { acc, value -> acc + value }.collect {
                Logger.v(it)
            }
            flowOf(1, 2, 3).scan("a") { acc, value -> acc + value }.collect {
                Logger.d(it)
            }
            flowOf(1, 2, 3).runningFold(emptyList<Int>()) { acc, value -> acc + value }.collect {
                Logger.i(it)
            }
            flowOf(1, 2, 3).runningFold(listOf(0)) { acc, value -> acc + value }.collect {
                Logger.w(it)
            }
        }
    }

}
