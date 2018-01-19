package com.example.flowmahuang.kotlinpractice.module.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by flowmahuang on 2017/12/18.
 */

class MainPagePagerAdapter(fm: FragmentManager,
                           val fragmentList: List<Fragment>,
                           val pagerTitle: List<String>) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return pagerTitle[position]
    }
}