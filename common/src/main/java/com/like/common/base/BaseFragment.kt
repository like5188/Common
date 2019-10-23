package com.like.common.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 若把初始化内容放到lazyLoadData实现</br>
 * 就是采用Lazy方式加载的Fragment</br>
 * 若不需要Lazy加载则lazyLoadData方法内留空,初始化内容放到initViews即可</br>
 *
 * 注1:</br>
 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint。</br>
 *
 * 注2:</br>
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.</br>
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show</br>
 * eg:</br>
 * transaction.hide(aFragment);</br>
 * transaction.show(aFragment);</br>
 *
 * 要使用 ARouter 来接收参数，请在 onCreate 方法中加上：ARouter.getInstance().inject(this)。
 * 然后在需要为每一个参数声明一个字段，并使用 @Autowired 标注，这样 ARouter 会自动对字段进行赋值，无需主动获取
 */
abstract class BaseFragment : androidx.fragment.app.Fragment() {
    /**
     * 是否可见状态
     */
    var visible = false
    /**
     * 标志位，View已经初始化完成。
     */
    var prepared: Boolean = false
    /**
     * 是否第一次加载
     * 当设置isFirstLoad为true，下次显示该Fragment时，还会触发lazyLoadData()方法
     */
    var firstLoad = true
    lateinit var mContentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firstLoad = true
        mContentView = initViews(inflater, container, savedInstanceState)
        prepared = true
        return mContentView
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            visible = true
            onVisible()
        } else {
            visible = false
            onInvisible()
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            visible = true
            onVisible()
        } else {
            visible = false
            onInvisible()
        }
    }

    protected open fun onVisible() {
        lazyLoad()
    }

    protected open fun onInvisible() {
    }

    private fun lazyLoad() {
        if (!prepared || !visible || !firstLoad) {
            return
        }
        firstLoad = false
        lazyLoadData()
    }

    /**
     * 需要延迟加载的数据放到这里
     */
    protected open fun lazyLoadData() {}

    /**
     * 初始化Fragment的视图
     */
    protected abstract fun initViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

}