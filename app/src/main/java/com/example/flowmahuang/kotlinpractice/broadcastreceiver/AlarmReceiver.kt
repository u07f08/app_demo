package com.example.flowmahuang.kotlinpractice.broadcastreceiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.flowmahuang.kotlinpractice.R
import com.example.flowmahuang.kotlinpractice.module.AlarmConstantManager
import com.example.flowmahuang.kotlinpractice.view.MainPageActivity


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(AlarmConstantManager.ALARM_ID, 0)
        val isVibration = intent.getBooleanExtra(AlarmConstantManager.VIBRATOR_ID, false)
        val gotoMainActivity = Intent(context, MainPageActivity::class.java)
        gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, gotoMainActivity, PendingIntent.FLAG_ONE_SHOT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (isVibration) {
            val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                val vibrationTimeArray = longArrayOf(100, 350, 250, 150, 150, 150)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationTimeArray, intArrayOf(255, 255), -1))
                } else {
                    vibrator.vibrate(vibrationTimeArray, -1)
                }
            }
        }

        val notification = NotificationCompat.Builder(context, id.toString())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("吃藥提醒")
                .setContentText("該吃藥了")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        notificationManager.notify(id, notification)

        Log.e("AlarmReceiver", id.toString())
    }
}