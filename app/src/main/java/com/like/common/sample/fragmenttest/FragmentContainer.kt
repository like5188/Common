package com.like.common.sample.fragmenttest

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.common.base.addFragments
import com.like.common.base.showFragment
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityFragmentContainerBinding

class FragmentContainer : FragmentActivity() {
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
        addFragments(R.id.fragment_holder, 0, *fragments.toTypedArray())

//        mBinding.vp.adapter = ViewPagerAdapter(fragments, this)
    }

    fun showFragment1(view: View) {
        showFragment(fragments[0])
    }

    fun showFragment2(view: View) {
        showFragment(fragments[1])
    }

    fun showFragment3(view: View) {
        showFragment(fragments[2])
    }

    fun showFragment4(view: View) {
        showFragment(fragments[3])
    }

}