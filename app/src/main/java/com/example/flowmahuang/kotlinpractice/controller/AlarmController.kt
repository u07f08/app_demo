package com.example.flowmahuang.kotlinpractice.controller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.flowmahuang.kotlinpractice.module.AlarmConstantManager
import com.example.flowmahuang.kotlinpractice.module.adapter.AlarmItemRecyclerViewAdapter
import com.example.flowmahuang.kotlinpractice.module.preferences.AlarmPreferences
import com.example.flowmahuang.kotlinpractice.service.AlarmService

class AlarmController(val mContext: Context) {
    val TYPE_NOTIFY = 0
    val TYPE_VIBRATION = 1

    private lateinit var mRecyclerViewAdapter: AlarmItemRecyclerViewAdapter
    private val mAlarmPreferences = AlarmPreferences(mContext)

    init {
        firstUseSetting()
    }

    private fun firstUseSetting() {
        if (mAlarmPreferences.getFirstHour() == 0) {
            mAlarmPreferences.saveFirstTime(0, 0)
        }
        if (mAlarmPreferences.getSecondHour() == 0) {
            mAlarmPreferences.saveSecondTime(0, 0)
        }
        if (mAlarmPreferences.getThirdHour() == 0) {
            mAlarmPreferences.saveThirdTime(0, 0)
        }
        if (mAlarmPreferences.getTotalTimes() == 0) {
            mAlarmPreferences.saveTotalTimes(1)
        }
        val vibrator = mContext.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        mAlarmPreferences.saveIsHaveVibrator(vibrator.hasVibrator())
    }

    private fun setAlarmManager(id: Int, alarmSwitch: Boolean) {
        val intent = Intent(mContext, AlarmService::class.java)
        if (alarmSwitch) {
            intent.putExtra(AlarmConstantManager.ACTION_MODE, AlarmConstantManager.ACTION_OPEN)
        } else {
            intent.putExtra(AlarmConstantManager.ACTION_MODE, AlarmConstantManager.ACTION_CLOSE)
        }
        intent.putExtra(AlarmConstantManager.ALARM_ID, id)

        mContext.startService(intent)
    }

    fun setAlarmSwitch(isStart: Boolean) {
        mAlarmPreferences.saveAlarmSwitch(isStart)
        when (mAlarmPreferences.getTotalTimes()) {
            3 -> {
                setAlarmManager(AlarmConstantManager.ID_THIRD_ALARM, isStart)
                setAlarmManager(AlarmConstantManager.ID_SECOND_ALARM, isStart)
            }
            2 -> {
                setAlarmManager(AlarmConstantManager.ID_SECOND_ALARM, isStart)
            }
        }
    }

    fun getAlarmSwitch(): Boolean {
        return mAlarmPreferences.getAlarmSwitch()
    }

    fun setAlarmTotalTimes(totalTimes: Int) {
        val originSetting = mAlarmPreferences.getTotalTimes()
        mAlarmPreferences.saveTotalTimes(totalTimes)
        mRecyclerViewAdapter.itemCount = totalTimes
        mRecyclerViewAdapter.notifyDataSetChanged()

        if (originSetting < totalTimes) {
            for (i in (originSetting + 1)..totalTimes) {
                if (i == 2) {
                    setAlarmManager(AlarmConstantManager.ID_SECOND_ALARM, true)
                } else {
                    setAlarmManager(AlarmConstantManager.ID_THIRD_ALARM, true)
                }
            }
        } else {
            for (i in (totalTimes + 1)..originSetting) {
                if (i == 2) {
                    setAlarmManager(AlarmConstantManager.ID_SECOND_ALARM, false)
                } else {
                    setAlarmManager(AlarmConstantManager.ID_THIRD_ALARM, false)
                }
            }
        }
    }

    fun getAlarmTotalTimes(): Int {
        return mAlarmPreferences.getTotalTimes()
    }

    fun setNotifyType(type: Int): Boolean {
        var check = true
        when (type) {
            TYPE_NOTIFY -> mAlarmPreferences.saveVibratorSwitch(false)
            TYPE_VIBRATION -> {
                if (mAlarmPreferences.getIsHaveVibrator()) {
                    mAlarmPreferences.saveVibratorSwitch(true)
                } else {
                    check = false
                }
            }
        }
        return check
    }

    fun getNotifyType(): Boolean {
        return mAlarmPreferences.getVibratorSwitch()
    }

    fun setRecyclerViewAdapter(view: RecyclerView) {
        val timeArray = ArrayList<Map<String, Int>>()
        var map = HashMap<String, Int>()
        map.put("hour", mAlarmPreferences.getFirstHour())
        map.put("min", mAlarmPreferences.getFirstMin())
        timeArray.add(map)
        map = HashMap()
        map.put("hour", mAlarmPreferences.getSecondHour())
        map.put("min", mAlarmPreferences.getSecondMin())
        timeArray.add(map)
        map = HashMap()
        map.put("hour", mAlarmPreferences.getThirdHour())
        map.put("min", mAlarmPreferences.getThirdMin())
        timeArray.add(map)

        mRecyclerViewAdapter = AlarmItemRecyclerViewAdapter(mContext, recyclerViewAdapterCallback, timeArray)
        val layoutManager = LinearLayoutManager(mContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        view.layoutManager = layoutManager
        view.adapter = mRecyclerViewAdapter
        mRecyclerViewAdapter.notifyDataSetChanged()
    }

    private val recyclerViewAdapterCallback = object : AlarmItemRecyclerViewAdapter.AlarmItemSettingCallback {
        override fun alarmSetting(position: Int, hour: Int, min: Int) {
            var id = 0
            val isOpen = mAlarmPreferences.getAlarmSwitch()
            when (position) {
                0 -> {
                    mAlarmPreferences.saveFirstTime(hour, min)
                    id = AlarmConstantManager.ID_FIRST_ALARM
                }
                1 -> {
                    mAlarmPreferences.saveSecondTime(hour, min)
                    id = AlarmConstantManager.ID_SECOND_ALARM
                }
                2 -> {
                    mAlarmPreferences.saveThirdTime(hour, min)
                    id = AlarmConstantManager.ID_THIRD_ALARM
                }
            }
            if (isOpen) {
                setAlarmManager(id, true)
            }
        }
    }
}