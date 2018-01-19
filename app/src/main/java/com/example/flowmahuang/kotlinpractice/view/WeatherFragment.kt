package com.example.flowmahuang.kotlinpractice.view


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.flowmahuang.kotlinpractice.R
import com.example.flowmahuang.kotlinpractice.controller.WeatherController
import com.example.flowmahuang.kotlinpractice.module.network.apiweather.Weather
import java.util.*

class WeatherFragment : Fragment() {
    private lateinit var mWeatherRefreshLayout: SwipeRefreshLayout
    private lateinit var mWeatherRootRelativeLayout: RelativeLayout
    private lateinit var mWeatherErrorMessageText: TextView
    private lateinit var mWeatherDescriptionImage: ImageView
    private lateinit var mWeatherDescriptionCityCounty: TextView
    private lateinit var mWeatherDescriptionText: TextView
    private lateinit var mWeatherDetailTemperature: TextView
    private lateinit var mWeatherDetailTemperatureUnit: TextView
    private lateinit var mWeatherDetailUnitSwitch: Switch
    private lateinit var mWeatherDetailFeelTemperature: TextView
    private lateinit var mWeatherDetailHumidity: TextView
    private lateinit var mWeatherDetailUpdateDate: TextView

    private lateinit var mController: WeatherController


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v: View = inflater!!.inflate(R.layout.fragment_weather, container, false)

        mWeatherRefreshLayout = v.findViewById(R.id.weather_refresh_layout)
        mWeatherRootRelativeLayout = v.findViewById(R.id.weather_root_rl)
        mWeatherErrorMessageText = v.findViewById(R.id.weather_error_message_text)
        mWeatherDescriptionImage = v.findViewById(R.id.weather_description_image)
        mWeatherDescriptionCityCounty = v.findViewById(R.id.weather_description_city_county)
        mWeatherDescriptionText = v.findViewById(R.id.weather_description_text)
        mWeatherDetailTemperature = v.findViewById(R.id.weather_detail_temperature)
        mWeatherDetailTemperatureUnit = v.findViewById(R.id.weather_detail_temperature_unit)
        mWeatherDetailUnitSwitch = v.findViewById(R.id.weather_detail_unit_switch)
        mWeatherDetailFeelTemperature = v.findViewById(R.id.weather_detail_feel_temperature)
        mWeatherDetailHumidity = v.findViewById(R.id.weather_detail_humidity)
        mWeatherDetailUpdateDate = v.findViewById(R.id.weather_detail_update_date)

        mController = WeatherController(context, controllerCallback)
        mWeatherDetailTemperatureUnit.text = getString(R.string.weather_temperature_unit, getString(R.string.weather_c))
        mWeatherRefreshLayout.setOnRefreshListener(refreshListener)
        mWeatherDetailUnitSwitch.setOnCheckedChangeListener(switchChangeListener)

        mWeatherRootRelativeLayout.visibility = View.INVISIBLE
        mWeatherDetailUnitSwitch.isClickable = false
        mWeatherRefreshLayout.isRefreshing = true
        mController.getCountyWeather()

        return v
    }

    private fun errorMessageTextViesVisibility(message: String) {
        mWeatherErrorMessageText.text = message
        if (mController.getIsApiEverSuccess()) {
            mWeatherErrorMessageText.visibility = View.INVISIBLE
        } else {
            mWeatherErrorMessageText.visibility = View.VISIBLE
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.dialog_error_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_btn_ok), null)
                .create()
                .show()
    }


    /**
     *  Callback Func
     */
    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        mWeatherRefreshLayout.isRefreshing = true
        mWeatherDetailUnitSwitch.isClickable = false
        mController.getCountyWeather()
    }

    private val switchChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val realTemp = mController.temperatureUnitChange(isChecked)
        val felTemp = mController.felTempUnitChange(isChecked)

        mWeatherDetailTemperature.text = getString(R.string.weather_temperature, realTemp)
        mWeatherDetailFeelTemperature.text = getString(R.string.weather_temperature, felTemp)
        mWeatherDetailTemperatureUnit.text = if (isChecked) {
            getString(R.string.weather_temperature_unit, getString(R.string.weather_f))
        } else {
            getString(R.string.weather_temperature_unit, getString(R.string.weather_c))
        }
    }

    private val controllerCallback = object : WeatherController.WeatherControllerCallback {
        override fun apiComplete(city: String, county: String, weatherDetail: Weather) {
            mWeatherRootRelativeLayout.visibility = View.VISIBLE
            mWeatherErrorMessageText.visibility = View.INVISIBLE
            mWeatherRefreshLayout.isRefreshing = false
            mWeatherDetailUnitSwitch.isClickable = true
            mWeatherDescriptionCityCounty.text = getString(R.string.weather_city_county, city, county)
            mWeatherDescriptionText.text = weatherDetail.getDesc()
            mWeatherDetailFeelTemperature.text = getString(R.string.weather_temperature, weatherDetail.getFeltAirTemp())
            mWeatherDetailTemperature.text = getString(R.string.weather_temperature, weatherDetail.getTemperature())
            mWeatherDetailHumidity.text = getString(R.string.weather_humidity, weatherDetail.getHumidity())
            mWeatherDetailUpdateDate.text = getString(R.string.weather_update_date_text, weatherDetail.getAt())

            val nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val decs = weatherDetail.getDesc()
            when {
                decs!!.contains("雨") -> mWeatherDescriptionImage.setImageResource(R.drawable.weather_rain)
                decs == "陰天" -> mWeatherDescriptionImage.setImageResource(R.drawable.weather_cloud)
                decs == "多雲" -> {
                    if (nowHour >= 18 || nowHour < 6) {
                        mWeatherDescriptionImage.setImageResource(R.drawable.weather_cloud_moon)
                    } else {
                        mWeatherDescriptionImage.setImageResource(R.drawable.weather_cloud_sun)
                    }
                }
                else -> {
                    if (nowHour >= 18 || nowHour < 6) {
                        mWeatherDescriptionImage.setImageResource(R.drawable.weather_moon)
                    } else {
                        mWeatherDescriptionImage.setImageResource(R.drawable.weather_sun)
                    }
                }
            }
        }

        override fun apiFailure(message: String) {
            mWeatherRefreshLayout.isRefreshing = false
            mWeatherDetailUnitSwitch.isClickable = true
            errorMessageTextViesVisibility(getString(R.string.network_error, message))
            showAlertDialog(getString(R.string.network_error, message))
        }

        override fun permissionDeny() {
            mWeatherRefreshLayout.isRefreshing = false
            mWeatherDetailUnitSwitch.isClickable = true
            mWeatherErrorMessageText.text = getString(R.string.permission_deny)
            mWeatherErrorMessageText.visibility = View.VISIBLE
            mWeatherRootRelativeLayout.visibility = View.INVISIBLE
        }

        override fun locationFailure() {
            mWeatherRefreshLayout.isRefreshing = false
            mWeatherDetailUnitSwitch.isClickable = true
            errorMessageTextViesVisibility(getString(R.string.location_error))
            showAlertDialog(getString(R.string.location_error))
        }
    }
}