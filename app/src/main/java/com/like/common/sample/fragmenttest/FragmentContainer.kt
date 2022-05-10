package com.like.common.sample.fragmenttest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.common.sample.R
import com.like.common.sample.activitytest.TestActivity
import com.like.common.sample.databinding.ActivityFragmentContainerBinding
import com.like.common.util.ApplicationHolder
import com.like.common.util.createIntent

class FragmentContainer : FragmentActivity() {
    companion object {
        private val TAG = FragmentContainer::class.java.simpleName
        fun start(context: Context? = null) {
            val ctx = context ?: ApplicationHolder.application
            val intent = ctx.createIntent<FragmentContainer>()
            ctx.startActivity(intent)
        }
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityFragmentContainerBinding>(this, R.layout.activity_fragment_container)
    }
    private val fragments = listOf(
            Fragment1(),
            Fragment2(),
            Fragment3(),
            Fragment4()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
//        addFragments(R.id.fragment_holder, 0, *fragments.toTypedArray())

        mBinding.vp.adapter = ViewPagerAdapter(fragments, this)
        mBinding.vp.offscreenPageLimit = fragments.size - 1// 避免fragment被销毁导致重新懒加载
        Log.e(TAG, "onCreate")
    }

    fun showFragment1(view: View) {
//        showFragment(fragments[0])
        mBinding.vp.setCurrentItem(0, false)
    }

    fun showFragment2(view: View) {
//        showFragment(fragments[1])
        mBinding.vp.setCurrentItem(1, false)
    }

    fun showFragment3(view: View) {
//        showFragment(fragments[2])
        mBinding.vp.setCurrentItem(2, false)
    }

    fun showFragment4(view: View) {
//        showFragment(fragments[3])
        mBinding.vp.setCurrentItem(3, false)
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    /***************************非生命周期方法，需要一定的条件来触发***************************/
    override fun onRestart() {
        super.onRestart()
        // 有 onStop() 状态恢复的时候触发
        Log.e(TAG, "onRestart")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 触发条件：首先是设置了启动模式，并且实例已经存在
        // 1、如果启动模式为 SingleTask 和 SingleInstance，再次启动该 Activity。
        // 2、这个实例位于栈顶，且启动模式为 SingleTop，再次启动该 Activity。
        Log.e(TAG, "onNewIntent")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 触发条件：
        // 1、当设置android:configChanges="orientation"时，横竖屏切换。注意：不会触发其它生命周期方法。
        Log.e(TAG, "onConfigurationChanged")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 触发条件：
        // 1、当不设置android:configChanges="orientation"时，横竖屏切换。
        // 2、按 home、menu、锁屏 键
        // 3、启动其它 Activity
        // 但是当用户主动去销毁一个 Activity 时，例如在应用中按返回键，onSaveInstanceState() 就不会被调用，所以用做数据的持久化保存，更应该在 onPause() 方法中进行。
        Log.e(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 触发条件：
        // 1、当不设置android:configChanges="orientation"时，横竖屏切换
        Log.e(TAG, "onRestoreInstanceState")
    }
}