package com.example.flowmahuang.kotlinpractice.module.network.apiweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by flowmahuang on 2018/1/3.
 */
class History {
    @SerializedName("img")
    @Expose
    private var img: String? = null
    @SerializedName("desc")
    @Expose
    private var desc: String? = null
    @SerializedName("temperature")
    @Expose
    private var temperature: Int? = null
    @SerializedName("felt_air_temp")
    @Expose
    private var feltAirTemp: Int? = null
    @SerializedName("humidity")
    @Expose
    private var humidity: Int? = null
    @SerializedName("rainfall")
    @Expose
    private var rainfall: Double? = null
    @SerializedName("sunrise")
    @Expose
    private var sunrise: String? = null
    @SerializedName("sunset")
    @Expose
    private var sunset: String? = null
    @SerializedName("at")
    @Expose
    private var at: String? = null

    fun getImg(): String? {
        return img
    }

    fun setImg(img: String) {
        this.img = img
    }

    fun getDesc(): String? {
        return desc
    }

    fun setDesc(desc: String) {
        this.desc = desc
    }

    fun getTemperature(): Int? {
        return temperature
    }

    fun setTemperature(temperature: Int?) {
        this.temperature = temperature
    }

    fun getFeltAirTemp(): Int? {
        return feltAirTemp
    }

    fun setFeltAirTemp(feltAirTemp: Int?) {
        this.feltAirTemp = feltAirTemp
    }

    fun getHumidity(): Int? {
        return humidity
    }

    fun setHumidity(humidity: Int?) {
        this.humidity = humidity
    }

    fun getRainfall(): Double? {
        return rainfall
    }

    fun setRainfall(rainfall: Double?) {
        this.rainfall = rainfall
    }

    fun getSunrise(): String? {
        return sunrise
    }

    fun setSunrise(sunrise: String) {
        this.sunrise = sunrise
    }

    fun getSunset(): String? {
        return sunset
    }

    fun setSunset(sunset: String) {
        this.sunset = sunset
    }

    fun getAt(): String? {
        return at
    }

    fun setAt(at: String) {
        this.at = at
    }
}