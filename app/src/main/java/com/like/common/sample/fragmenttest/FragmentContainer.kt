package com.like.common.sample.fragmenttest

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityFragmentContainerBinding

class FragmentContainer : FragmentActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityFragmentContainerBinding>(this, R.layout.activity_fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        val fragment1 = Fragment1()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_holder, fragment1)
            hide(fragment1)
        }.commit()
        supportFragmentManager.beginTransaction().apply {
            show(fragment1)
        }.commit()
    }
}