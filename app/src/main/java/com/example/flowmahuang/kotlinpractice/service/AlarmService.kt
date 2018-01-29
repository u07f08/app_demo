package com.example.flowmahuang.kotlinpractice.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.flowmahuang.kotlinpractice.broadcastreceiver.AlarmReceiver
import com.example.flowmahuang.kotlinpractice.module.AlarmConstantManager
import com.example.flowmahuang.kotlinpractice.module.preferences.AlarmPreferences
import java.util.*

class AlarmService : Service() {
    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mAlarmPreferences: AlarmPreferences

    override fun onCreate() {
        super.onCreate()
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmPreferences = AlarmPreferences(this@AlarmService)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent) {
            Log.e("service", "intent was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags))
            return Service.START_REDELIVER_INTENT
        }
        val action = intent.getIntExtra(AlarmConstantManager.ACTION_MODE, AlarmConstantManager.ACTION_CLOSE)
        if (action == AlarmConstantManager.ACTION_OPEN) {
            setAlarmOpen(intent.getIntExtra(AlarmConstantManager.ALARM_ID, 0))
        } else {
            setAlarmClose(intent.getIntExtra(AlarmConstantManager.ALARM_ID, 0))
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun setAlarmOpen(id: Int) {
        val calendar = Calendar.getInstance()
        val mAlarmIntent = Intent(this@AlarmService, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent

        val settingHour: Int
        val settingMin: Int
        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nowMin = calendar.get(Calendar.MINUTE)

        when (id) {
            AlarmConstantManager.ID_FIRST_ALARM -> {
                settingHour = Integer.valueOf(mAlarmPreferences.getFirstHour())
                settingMin = Integer.valueOf(mAlarmPreferences.getFirstMin())
            }
            AlarmConstantManager.ID_SECOND_ALARM -> {
                settingHour = Integer.valueOf(mAlarmPreferences.getSecondHour())
                settingMin = Integer.valueOf(mAlarmPreferences.getSecondMin())
            }
            else -> {
                settingHour = Integer.valueOf(mAlarmPreferences.getThirdHour())
                settingMin = Integer.valueOf(mAlarmPreferences.getFirstMin())
            }
        }
        mAlarmIntent.putExtra(AlarmConstantManager.VIBRATOR_ID, mAlarmPreferences.getVibratorSwitch())
        mAlarmIntent.putExtra(AlarmConstantManager.ALARM_ID, id)
        pendingIntent = PendingIntent.getBroadcast(this, id, mAlarmIntent, PendingIntent.FLAG_ONE_SHOT)

        calendar.set(Calendar.HOUR_OF_DAY, settingHour)
        calendar.set(Calendar.MINUTE, settingMin)
        calendar.set(Calendar.SECOND, 0)
        if (nowHour >= settingHour && nowMin >= settingMin) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                86400000, pendingIntent)
    }

    private fun setAlarmClose(id: Int) {
        val intent = Intent(this@AlarmService, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_ONE_SHOT)

        mAlarmManager.cancel(pendingIntent)
    }
}