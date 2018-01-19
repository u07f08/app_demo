package com.example.flowmahuang.kotlinpractice.view

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.flowmahuang.kotlinpractice.R
import com.example.flowmahuang.kotlinpractice.module.adapter.MainPagePagerAdapter
import com.example.flowmahuang.kotlinpractice.module.permission.PermissionsActivity
import com.example.flowmahuang.kotlinpractice.module.permission.PermissionsChecker
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {
    private val checkPermissionList: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val ASK_PERMISSION_CODE: Int = 0

    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var fragmentTitle: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val permissionsChecker = PermissionsChecker(this)
        if (permissionsChecker.missingPermissions(* checkPermissionList)) {
            PermissionsActivity.startPermissionsForResult(this,
                    ASK_PERMISSION_CODE,
                    * checkPermissionList)
        }

        fragmentInit()
        val pagerAdapter = MainPagePagerAdapter(supportFragmentManager, fragmentList, fragmentTitle)

        main_page_pager.adapter = pagerAdapter
        main_page_sliding_tabs.setDistributeEvenly(true)
        main_page_sliding_tabs.setViewPager(main_page_pager)
    }

    private fun fragmentInit() {
        fragmentList = ArrayList()
        fragmentTitle = ArrayList()
        val weatherFragment = WeatherFragment()
        val mediaListFragment = MediaListFragment()
        val alarmFragment = AlarmFragment()

        fragmentList.add(weatherFragment)
        fragmentList.add(mediaListFragment)
        fragmentList.add(alarmFragment)
        fragmentTitle.add(getString(R.string.main_page_weather_title))
        fragmentTitle.add(getString(R.string.main_page_file_title))
        fragmentTitle.add(getString(R.string.main_page_alarm_title))
    }
}