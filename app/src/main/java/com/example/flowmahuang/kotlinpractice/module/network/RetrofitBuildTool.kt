package com.example.flowmahuang.kotlinpractice.module.network

import com.example.flowmahuang.kotlinpractice.module.network.apicity.City
import com.example.flowmahuang.kotlinpractice.module.network.apiweather.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuildTool(val callback: WeatherApiCallback) {
    interface WeatherApiCallback {
        fun apiAllCountyAndCity(cityList: List<City>)

        fun apiNowWeather(weather: Weather)

        fun apiFailure(throwMessage: String)
    }

    private val API_BASE_URL = "https://works.ioa.tw/weather/api/"
    private val mRetrofit by lazy { create() }

    private fun create(): WeatherApiService {
        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(WeatherApiService::class.java)
    }

    fun getAllCountyAndCity() {
        val call = mRetrofit.getAllCountyCity()
        call.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>?, response: Response<List<City>>?) {
                if (response != null) {
                    callback.apiAllCountyAndCity(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<City>>?, t: Throwable?) {
                if (t != null) {
                    callback.apiFailure(t.message!!)
                }
            }
        })
    }

    fun getNowWeather(areaId: String) {
        val call = mRetrofit.getAreaWeather(areaId)
        call.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {
                if (response != null) {
                    callback.apiNowWeather(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Weather>?, t: Throwable?) {
                if (t != null) {
                    callback.apiFailure(t.message!!)
                }
            }
        })
    }
}