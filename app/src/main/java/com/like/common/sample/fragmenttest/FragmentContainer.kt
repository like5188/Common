package com.like.common.sample.fragmenttest

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityFragmentContainerBinding
import com.like.common.util.ApplicationHolder
import com.like.common.util.createIntent

class FragmentContainer : FragmentActivity() {
    companion object {
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

}