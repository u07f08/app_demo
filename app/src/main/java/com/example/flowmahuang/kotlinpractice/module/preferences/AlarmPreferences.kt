package com.example.flowmahuang.kotlinpractice.module.preferences

import android.content.Context

class AlarmPreferences(context: Context) : PreferencesHelper(context) {
    private val SP_FiLE_NAME = AlarmPreferences::class.java.name
    private val SP_ALARM_SWITCH = "SP_ALARM_SWITCH"
    private val SP_IS_HAVE_VIBRATOR = "SP_IS_HAVE_VIBRATOR"
    private val SP_VIBRATOR_SWITCH = "SP_VIBRATOR_SWITCH"
    private val SP_FIRST_HOUR = "SP_FIRST_HOUR"
    private val SP_FIRST_MIN = "SP_FIRST_MIN"
    private val SP_SECOND_HOUR = "SP_SECOND_HOUR"
    private val SP_SECOND_MIN = "SP_SECOND_MIN"
    private val SP_THIRD_HOUR = "SP_THIRD_HOUR"
    private val SP_THIRD_MIN = "SP_THIRD_MIN"
    private val SP_TOTAL_TIMES = "SP_TOTAL_TIMES"

    fun saveAlarmSwitch(boolean: Boolean) {
        save(PreferencesHelper.Type.BOOLEAN, SP_ALARM_SWITCH, boolean)
    }

    fun getAlarmSwitch(): Boolean {
        return get(SP_ALARM_SWITCH, PreferencesHelper.Type.BOOLEAN) as Boolean
    }

    fun saveIsHaveVibrator(boolean: Boolean){
        save(PreferencesHelper.Type.BOOLEAN, SP_IS_HAVE_VIBRATOR, boolean)
    }

    fun getIsHaveVibrator(): Boolean {
        return get(SP_IS_HAVE_VIBRATOR, PreferencesHelper.Type.BOOLEAN) as Boolean
    }

    fun saveVibratorSwitch(boolean: Boolean) {
        save(PreferencesHelper.Type.BOOLEAN, SP_VIBRATOR_SWITCH, boolean)
    }

    fun getVibratorSwitch(): Boolean {
        return get(SP_VIBRATOR_SWITCH, PreferencesHelper.Type.BOOLEAN) as Boolean
    }

    fun saveFirstTime(hour: Int, min: Int) {
        save(PreferencesHelper.Type.INT, SP_FIRST_HOUR, hour)
        save(PreferencesHelper.Type.INT, SP_FIRST_MIN, min)
    }

    fun getFirstHour(): Int {
        return get(SP_FIRST_HOUR, PreferencesHelper.Type.INT) as Int
    }

    fun getFirstMin(): Int {
        return get(SP_FIRST_MIN, PreferencesHelper.Type.INT) as Int
    }

    fun saveSecondTime(hour: Int, min: Int) {
        save(PreferencesHelper.Type.INT, SP_SECOND_HOUR, hour)
        save(PreferencesHelper.Type.INT, SP_SECOND_MIN, min)
    }

    fun getSecondHour(): Int {
        return get(SP_SECOND_HOUR, PreferencesHelper.Type.INT) as Int
    }

    fun getSecondMin(): Int {
        return get(SP_SECOND_MIN, PreferencesHelper.Type.INT) as Int
    }

    fun saveThirdTime(hour: Int, min: Int) {
        save(PreferencesHelper.Type.INT, SP_THIRD_HOUR, hour)
        save(PreferencesHelper.Type.INT, SP_THIRD_MIN, min)
    }

    fun getThirdHour(): Int {
        return get(SP_THIRD_HOUR, PreferencesHelper.Type.INT) as Int
    }

    fun getThirdMin(): Int {
        return get(SP_THIRD_MIN, PreferencesHelper.Type.INT) as Int
    }

    fun saveTotalTimes(totalTimes: Int) {
        save(PreferencesHelper.Type.INT, SP_TOTAL_TIMES, totalTimes)
    }

    fun getTotalTimes(): Int {
        return get(SP_TOTAL_TIMES, PreferencesHelper.Type.INT) as Int
    }

    override fun getClassName(): String {
        return SP_FiLE_NAME
    }
}