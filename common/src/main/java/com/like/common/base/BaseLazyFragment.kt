package com.like.common.base

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import java.util.concurrent.atomic.AtomicBoolean

/**
 * androidx 下 Fragment 的懒加载数据封装
 *
 * 懒加载的问题：由于 [Fragment] 的 [onResume] 方法，在不可见的时候也会被调用，不符合逻辑。
 * 因此，我们要处理相关的逻辑，来让只有当前可见的那个 [Fragment] 才调用 [onResume] 方法。
 *
 * 注1:与 ViewPager 一起使用</br>
 * 1、传统方式（废弃）</br>
 * 使用 [androidx.viewpager.widget.ViewPager] + [androidx.fragment.app.FragmentPagerAdapter]</br>
 * 此时 Fragment 的显示隐藏是由 [androidx.fragment.app.FragmentPagerAdapter] 调用的 [setUserVisibleHint] 方法。</br>
 *
 * 2、新的方式</br>
 * 使用 [androidx.viewpager2.widget.ViewPager2] + [androidx.viewpager2.adapter.FragmentStateAdapter]</br>
 * 它已经利用 [androidx.fragment.app.FragmentTransaction.setMaxLifecycle] 处理好了 [onResume] 方法的调用，只有当前可见的 Fragment 才会调用。</br>
 *
 * 注2:add+show+hide</br>
 * 1、传统方式（废弃）</br>
 * 通过 [androidx.fragment.app.FragmentTransaction] 的 show 和 hide 的方法来控制显示隐藏，调用的是[onHiddenChanged]方法。</br>
 * 针对初始就 show 的 Fragment 为了触发[onHiddenChanged]事件 达到lazy效果 需要先 hide 再 show。</br>
 *
 * 2、新的方式
 * 利用 [androidx.fragment.app.FragmentTransaction.setMaxLifecycle] 方法来处理 [onResume] 方法的调用。
 * 我们使用扩展方法 [addFragments] 添加 Fragment ，用 [showFragment] 控制显示指定的 Fragment。
 */
abstract class BaseLazyFragment : Fragment() {
    private val isLoaded = AtomicBoolean(false)

    override fun onResume() {
        super.onResume()
        if (isLoaded.compareAndSet(false, true)) {
            onLazyLoadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded.compareAndSet(true, false)
    }

    /**
     * 需要延迟加载数据的操作放到这里
     */
    abstract fun onLazyLoadData()
}

/**
 * 添加 Fragment
 * @param containerViewId   装载 Fragment 的容器 id
 * @param showPosition      默认显示的角标
 * @param fragments         需要添加的 fragment
 */
fun Fragment.addFragments(
    @IdRes containerViewId: Int,
    showPosition: Int = 0,
    vararg fragments: Fragment
) {
    addFragments(containerViewId, showPosition, childFragmentManager, *fragments)
}

/**
 * 显示目标 [fragment]，并隐藏其他 fragment
 */
fun Fragment.showFragment(fragment: Fragment) {
    showFragment(childFragmentManager, fragment)
}

/**
 * 移除目标 [fragment]
 */
fun Fragment.removeFragment(fragment: Fragment) {
    removeFragment(childFragmentManager, fragment)
}

/**
 * 添加 Fragment
 * @param containerViewId   装载 Fragment 的容器id
 * @param showPosition      默认显示的角标
 * @param fragments         需要添加到的 fragment
 */
fun FragmentActivity.addFragments(
    @IdRes containerViewId: Int,
    showPosition: Int = 0,
    vararg fragments: Fragment
) {
    addFragments(containerViewId, showPosition, supportFragmentManager, *fragments)
}

/**
 * 显示目标 [fragment]，并隐藏其他 fragment
 */
fun FragmentActivity.showFragment(fragment: Fragment) {
    showFragment(supportFragmentManager, fragment)
}

/**
 * 移除目标 [fragment]
 */
fun FragmentActivity.removeFragment(fragment: Fragment) {
    removeFragment(supportFragmentManager, fragment)
}

/**
 * 添加 [fragments] ，并默认显示位置 [showPosition] 位置的 [Fragment]，并设置其最大 Lifecycle 为 [Lifecycle.State.RESUMED]，
 * 其他隐藏的 [Fragment]，设置其最大 Lifecycle 为 [Lifecycle.State.STARTED]
 * 注意：会根据 [fragment.javaClass.name] 作为 tag 来避免重复添加。
 */
private fun addFragments(
    @IdRes containerViewId: Int,
    showPosition: Int,
    fragmentManager: FragmentManager,
    vararg fragments: Fragment
) {
    if (fragments.isEmpty()) {
        return
    }
    fragmentManager.beginTransaction().apply {
        for (index in fragments.indices) {
            val fragment = fragments[index]
            val tag = fragment.javaClass.name
            // 防止重复添加
            if (fragmentManager.findFragmentByTag(tag) == null) {
                add(containerViewId, fragment, tag)
                if (showPosition == index) {
                    setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                } else {
                    hide(fragment)
                    setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                }
            }
        }
    }.commit()
}

/**
 * 显示需要显示的 [showFragment]，并设置其最大 Lifecycle 为 [Lifecycle.State.RESUMED]，
 * 同时隐藏其他 Fragment,并设置其最大 Lifecycle 为 [Lifecycle.State.STARTED]
 */
private fun showFragment(fragmentManager: FragmentManager, showFragment: Fragment) {
    fragmentManager.beginTransaction().apply {
        show(showFragment)
        setMaxLifecycle(showFragment, Lifecycle.State.RESUMED)

        //获取其中所有的fragment,其他的fragment进行隐藏
        for (fragment in fragmentManager.fragments) {
            if (fragment != showFragment) {
                hide(fragment)
                setMaxLifecycle(fragment, Lifecycle.State.STARTED)
            }
        }
    }.commit()
}

/**
 * 移除目标 [fragment]
 */
private fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment) {
    fragmentManager.beginTransaction().apply {
        remove(fragment)
    }.commit()
}
