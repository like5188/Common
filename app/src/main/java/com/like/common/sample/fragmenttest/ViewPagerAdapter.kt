package com.like.common.sample.fragmenttest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(private val fragments: List<Fragment>, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = fragments[p0]

    override fun getCount(): Int = fragments.size

}