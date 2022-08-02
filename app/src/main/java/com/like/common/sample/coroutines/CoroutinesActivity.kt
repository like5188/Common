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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

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
