package com.example.flowmahuang.kotlinpractice.controller

import android.Manifest
import android.app.Activity
import android.os.Build
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import com.example.flowmahuang.kotlinpractice.module.GetMediaUri
import com.example.flowmahuang.kotlinpractice.module.adapter.MediaSelectRecyclerViewAdapter
import com.example.flowmahuang.kotlinpractice.module.permission.PermissionsChecker

class MediaListController(val mActivity: Activity, val mControllerCallback: MediaListControllerCallback) {
    interface MediaListControllerCallback {
        fun isSelectAll(isSelect: Boolean)

        fun isPermissionDeny()
    }

    private val checkPermissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var mRecyclerViewAdapter: MediaSelectRecyclerViewAdapter
    private var mHeaderSelect = false

    private fun containerHeight(): Int {
        val dm = DisplayMetrics()
        mActivity.windowManager.defaultDisplay.getMetrics(dm)

        Log.d("width", "Screen Height of " + Build.MANUFACTURER + " " + Build.DEVICE + " "
                + Build.MODEL + " is " + Integer.toString(dm.widthPixels))

        //get predefined value
        val ratio = 3.0

        return (dm.widthPixels / ratio).toInt()
    }

    fun getMediaUriArray(recyclerView: RecyclerView) {
        val permissionsChecker = PermissionsChecker(mActivity)
        if (permissionsChecker.missingPermissions(* checkPermissionList)) {
            mControllerCallback.isPermissionDeny()
        } else {
            GetMediaUri.create(mActivity) { mediaArrayList ->
                val gridLayoutManager = GridLayoutManager(mActivity, 3)
                mRecyclerViewAdapter = MediaSelectRecyclerViewAdapter(
                        mActivity,
                        recyclerView,
                        gridLayoutManager,
                        mediaArrayList,
                        containerHeight(),
                        isSelectAllCallback)
                recyclerView.layoutManager = gridLayoutManager
                recyclerView.adapter = mRecyclerViewAdapter
                recyclerView.invalidate()
            }
        }
    }

    fun setAdapterHeader(): Boolean {
        mHeaderSelect = !mHeaderSelect
        mRecyclerViewAdapter.setHeaderSelect(mHeaderSelect)

        return mHeaderSelect
    }

    /**
     *  Callback Func
     */
    private val isSelectAllCallback = MediaSelectRecyclerViewAdapter.isSelectAllCallback { isSelect ->
        if (isSelect) {
            if (mHeaderSelect) return@isSelectAllCallback
            mHeaderSelect = true
            mControllerCallback.isSelectAll(true)
        } else {
            if (!mHeaderSelect) return@isSelectAllCallback
            mHeaderSelect = false
            mControllerCallback.isSelectAll(false)
        }
    }
}