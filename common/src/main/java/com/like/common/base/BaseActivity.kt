package com.like.common.base

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

/**
 * 封装了双击返回键退出程序功能
 */
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        /**
         * 双击返回键退出程序的时间间隔
         */
        private const val INTERVAL_DOUBLE_PRESS_BACK_TO_EXIT = 2000L
    }

    /**
     * 是否第一次按下返回键，用于双击退出
     */
    private var isFirstPressBack: Boolean = true
    private val doublePressBackToExitHandler = Handler(Handler.Callback {
        isFirstPressBack = true
        true
    })

    override fun onBackPressed() {
        // 双击返回键退出程序
        if (isSupportDoublePressBackToExit() && isFirstPressBack) {
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show()
            isFirstPressBack = false
            doublePressBackToExitHandler.sendEmptyMessageDelayed(0,
                INTERVAL_DOUBLE_PRESS_BACK_TO_EXIT
            )
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        doublePressBackToExitHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 如果需要双击退出界面，请重写此方法
     *
     * @return
     */
    protected open fun isSupportDoublePressBackToExit(): Boolean {
        return false
    }

}