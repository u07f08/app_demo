package com.example.flowmahuang.kotlinpractice.controller

import android.Manifest
import android.content.Context
import com.example.flowmahuang.kotlinpractice.module.location.GetAddressCounty
import com.example.flowmahuang.kotlinpractice.module.network.RetrofitBuildTool
import com.example.flowmahuang.kotlinpractice.module.network.apicity.City
import com.example.flowmahuang.kotlinpractice.module.network.apiweather.Weather
import com.example.flowmahuang.kotlinpractice.module.permission.PermissionsChecker

class WeatherController(val mContext: Context, val mControllerCallback: WeatherControllerCallback) {
    interface WeatherControllerCallback {
        fun apiComplete(city: String, county: String, weatherDetail: Weather)

        fun apiFailure(message: String)

        fun permissionDeny()

        fun locationFailure()
    }

    private val WEATHER_UNIT_C = false
    private val WEATHER_UNIT_F = true
    private val checkPermissionList:Array<String> = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var mLocationModule = GetAddressCounty(mContext, locationCallback())
    private var mApiModule: RetrofitBuildTool = RetrofitBuildTool(apiCallback())

    private var mCityList: List<City>? = null
    private var mWeatherInformation: Weather? = null
    private var mCityName: String = ""
    private var mCountyName: String = ""
    private var isApiEverSuccess = false

    private fun getCountyId(): String {
        if (mCityList != null && !mCityName.isEmpty() && !mCountyName.isEmpty()) {
            mCityList!!.forEach {
                if (!it.getName().contains("縣") || !it.getName().contains("市")) {
                    if (it.getTowns()!![0].getName().endsWith("區")) {
                        it.setName(it.getName() + "市")
                    } else {
                        it.setName(it.getName() + "縣")
                    }
                }

                if (it.getName() == mCityName) {
                    it.getTowns()!!.forEach {
                        if (it.getName() == mCountyName) {
                            return it.getId()
                        }
                    }
                }
            }
        }
        return ""
    }

    private fun getAreaWeather() {
        val id: String = getCountyId()
        if (!id.isEmpty()) {
            mApiModule.getNowWeather(id)
        }
    }

    fun getCountyWeather() {
        val permissionsChecker = PermissionsChecker(mContext)
        if (permissionsChecker.missingPermissions(* checkPermissionList)) {
            mControllerCallback.permissionDeny()
        } else {
            mLocationModule.requestUserLocation()
            mApiModule.getAllCountyAndCity()
        }
    }

    fun getIsApiEverSuccess(): Boolean {
        return isApiEverSuccess
    }

    fun temperatureUnitChange(switchIsCheck: Boolean): Int {
        val returnTemperature: Int? = mWeatherInformation?.getTemperature()
        return if (returnTemperature == null) {
            0
        } else {
            if (switchIsCheck == WEATHER_UNIT_C) {
                returnTemperature
            } else {
                (returnTemperature * 9 / 5) + 32
            }
        }
    }

    fun felTempUnitChange(switchIsCheck: Boolean): Int {
        val returnTemperature: Int? = mWeatherInformation?.getFeltAirTemp()
        return if (returnTemperature == null) {
            0
        } else {
            if (switchIsCheck == WEATHER_UNIT_C) {
                returnTemperature
            } else {
                (returnTemperature * 9 / 5) + 32
            }
        }
    }

    /**
     *  Callback Func
     */
    private fun apiCallback(): RetrofitBuildTool.WeatherApiCallback {
        return object : RetrofitBuildTool.WeatherApiCallback {
            override fun apiAllCountyAndCity(cityList: List<City>) {
                mCityList = cityList
                getAreaWeather()
            }

            override fun apiNowWeather(weather: Weather) {
                isApiEverSuccess = true
                mWeatherInformation = weather
                mControllerCallback.apiComplete(mCityName, mCountyName, weather)
            }

            override fun apiFailure(throwMessage: String) {
                mControllerCallback.apiFailure(throwMessage)
            }
        }
    }

    private fun locationCallback(): GetAddressCounty.CountyNameCallback {
        return object : GetAddressCounty.CountyNameCallback {
            override fun isPermissionDeny() {
            }

            override fun getLocationFail() {
                mControllerCallback.locationFailure()
            }

            override fun countyName(cityName: String, countyName: String) {
                mCityName = cityName
                mCountyName = countyName
                getAreaWeather()
            }
        }
    }
}