package com.example.flowmahuang.kotlinpractice.module.adapter

import android.app.TimePickerDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.flowmahuang.kotlinpractice.R
import java.util.*

class AlarmItemRecyclerViewAdapter(
        val mContext: Context,
        val mCallback: AlarmItemSettingCallback,
        val initTime: ArrayList<Map<String, Int>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface AlarmItemSettingCallback {
        fun alarmSetting(position: Int, hour: Int, min: Int)
    }

    private val MIN_CLICK_DELAY_TIME = 3000
    private var lastClickTime: Long = 0
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var mInitTimeArray: ArrayList<Map<String, Int>> = initTime
    private var itemCount = 1
    private var mItemViewHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = inflater.inflate(
                R.layout.item_alarm_time_setting,
                parent,
                false)
        return object : RecyclerView.ViewHolder(v) {}
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val view = holder!!.itemView
        if (mItemViewHeight != 0) {
            val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mItemViewHeight
            )
            view.layoutParams = params
        }

        val eatTimesView = view.findViewById<TextView>(R.id.item_alarm_eat_times)
        val timeView = view.findViewById<TextView>(R.id.item_alarm_notice_time)

        val hourOfDay = mInitTimeArray[position]["hour"]
        val minute = mInitTimeArray[position]["min"]
        val hourString = if (hourOfDay!! > 9) hourOfDay.toString() else "0" + hourOfDay.toString()
        val minString = if (minute!! > 9) minute.toString() else "0" + minute.toString()
        timeView.text = mContext.getString(R.string.alarm_time_text_format, hourString, minString)

        eatTimesView.text = mContext.getString(R.string.alarm_eat_times_text, (position + 1).toString())
        timeView.setOnClickListener {
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime

                val calendar = GregorianCalendar()
                val listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    mCallback.alarmSetting(position, hourOfDay, minute)
                    val map = HashMap<String, Int>()
                    map.put("hour", hourOfDay)
                    map.put("min", minute)
                    mInitTimeArray[position] = map

                    notifyItemChanged(position)
                }

                if (hourOfDay == 0 && minute == 0) {
                    TimePickerDialog(
                            mContext,
                            listener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    ).show()
                } else {
                    TimePickerDialog(
                            mContext,
                            listener,
                            hourOfDay,
                            minute,
                            false
                    ).show()
                }
            }
        }
    }

    fun setItemCount(item: Int) {
        itemCount = item
    }

    fun setItemHeight(height: Int) {
        mItemViewHeight = height
    }
}