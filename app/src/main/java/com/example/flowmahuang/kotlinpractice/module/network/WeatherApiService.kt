package com.example.flowmahuang.kotlinpractice.module.network

import com.example.flowmahuang.kotlinpractice.module.network.apicity.City
import com.example.flowmahuang.kotlinpractice.module.network.apiweather.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by flowmahuang on 2017/12/12.
 */
interface WeatherApiService {
    @GET("all.json")
    fun getAllCountyCity(): Call<List<City>>

    @GET("weathers/{id}.json")
    fun getAreaWeather(@Path("id") areaId:String) : Call<Weather>
}