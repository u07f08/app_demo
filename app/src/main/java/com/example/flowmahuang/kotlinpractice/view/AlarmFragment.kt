package com.example.flowmahuang.kotlinpractice.view


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.flowmahuang.kotlinpractice.R
import com.example.flowmahuang.kotlinpractice.controller.AlarmController

class AlarmFragment : Fragment() {
    private lateinit var mAlarmSwitch: SwitchCompat
    private lateinit var mAlarmSwitchTitle: TextView
    private lateinit var mAlarmRemindMethodButton: Button
    private lateinit var mAlarmRemindFrequencySpinner: Spinner
    private lateinit var mAlarmSettingTimeRecyclerView: RecyclerView
    private lateinit var mAlarmFuncShaded: View

    private lateinit var mController: AlarmController

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v: View = inflater!!.inflate(R.layout.fragment_alarm, container, false)
        mAlarmSwitch = v.findViewById(R.id.alarm_switch)
        mAlarmSwitchTitle = v.findViewById(R.id.alarm_switch_title)
        mAlarmRemindMethodButton = v.findViewById(R.id.alarm_remind_method_button)
        mAlarmRemindFrequencySpinner = v.findViewById(R.id.alarm_remind_frequency_spinner)
        mAlarmSettingTimeRecyclerView = v.findViewById(R.id.alarm_setting_time_recycler_view)
        mAlarmFuncShaded = v.findViewById(R.id.alarm_func_shaded)

        mController = AlarmController(context)

        initViewInformation()

        mController.setRecyclerViewAdapter(mAlarmSettingTimeRecyclerView)
        mController.setAlarmTotalTimes(1)
        mAlarmSwitch.setOnCheckedChangeListener(switchChangeListener)
        mAlarmRemindMethodButton.setOnClickListener(clickListener)
        mAlarmRemindFrequencySpinner.onItemSelectedListener = spinnerItemClickListener

        return v
    }

    private fun initViewInformation(){
        val switchText: String
        if (mController.getAlarmSwitch()) {
            switchText = getString(R.string.alarm_on)
            mAlarmFuncShaded.visibility = View.GONE
            mAlarmSwitch.isChecked = true
        } else {
            switchText = getString(R.string.alarm_off)
            mAlarmFuncShaded.visibility = View.VISIBLE
        }
        mAlarmSwitchTitle.text = getString(R.string.alarm_switch_text, switchText)

        if (mController.getNotifyType()) {
            mAlarmRemindMethodButton.text = getString(R.string.alarm_method_notify_vibrator)
        }

        val timesArray = ArrayAdapter.createFromResource(
                context,
                R.array.alarm_eat_total_times,
                android.R.layout.simple_spinner_dropdown_item
        )
        mAlarmRemindFrequencySpinner.adapter = timesArray
        mAlarmRemindFrequencySpinner.setSelection(mController.getAlarmTotalTimes() - 1)

    }

    private val switchChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val switchText: String
        if (isChecked) {
            mAlarmFuncShaded.visibility = View.GONE
            switchText = getString(R.string.alarm_on)
        } else {
            mAlarmFuncShaded.visibility = View.VISIBLE
            switchText = getString(R.string.alarm_off)
        }
        mController.setAlarmSwitch(isChecked)
        mAlarmSwitchTitle.text = getString(R.string.alarm_switch_text, switchText)
    }

    private val spinnerItemClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            mController.setAlarmTotalTimes(position + 1)
        }
    }

    private val clickListener = View.OnClickListener {
        val methodArray = arrayOf(
                getString(R.string.alarm_method_notify),
                getString(R.string.alarm_method_notify_vibrator)
        )
        AlertDialog.Builder(context)
                .setItems(methodArray, { dialog, which ->
                    if (mController.setNotifyType(which)) {
                        mAlarmRemindMethodButton.text = methodArray[which]
                    } else {
                        Toast.makeText(context, getString(R.string.alarm_not_has_vibrator), Toast.LENGTH_SHORT).show()
                    }
                })
                .create()
                .show()
    }
}
