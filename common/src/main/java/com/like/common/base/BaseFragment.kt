package com.like.common.base

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * 懒加载数据封装
 *
 * 注1:</br>
 * 如果是与 ViewPager 一起使用，Fragment 的显示隐藏是由 [FragmentPagerAdapter] 调用的 [setUserVisibleHint] 方法。</br>
 *
 * 注2:</br>
 * 如果是通过 FragmentTransaction 的 show 和 hide 的方法来控制显示隐藏，调用的是[onHiddenChanged]方法</br>
 * 针对初始就 show 的 Fragment 为了触发[onHiddenChanged]事件 达到lazy效果 需要先 hide 再 show </br>
 * eg:</br>
 * transaction.hide(aFragment);</br>
 * transaction.show(aFragment);</br>
 *
 * eg:</br>
 * @Autowired
 * @JvmField
 * var pageIndex: Int = 0
 */
abstract class BaseFragment : Fragment() {
    /**
     * Fragment 是否已经初始化完毕
     */
    private var isInitialized: Boolean = false

    /**
     * Fragment 是否对用户可见
     */
    var isVisibleToUser = false

    /**
     * 是否需要加载数据
     * 如果在第一次加载完成后，需要重新触发加载数据，可以设置[isNeedData]为true，那么下次显示该Fragment时，还会触发[onLazyLoadData]方法
     */
    var isNeedData = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isInitialized = true
        lazyLoadData()// 在这里调用是因为搭配 ViewPager 使用时，setUserVisibleHint()方法会在此方法之前调用，然后就无法触发懒加载了。
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.isVisibleToUser = !hidden
        if (!hidden) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    private fun lazyLoadData() {
        if (!isInitialized || !isVisibleToUser || !isNeedData) {
            return
        }
        isNeedData = false
        Log.d(this.javaClass.simpleName, "onLazyLoadData")
        onLazyLoadData()
    }

    /**
     * Fragment 可见
     */
    protected open fun onVisible() {
        lazyLoadData()
    }

    /**
     * Fragment 不可见
     */
    protected open fun onInvisible() {}

    /**
     * 需要延迟加载数据的操作放到这里
     */
    protected open fun onLazyLoadData() {}

}