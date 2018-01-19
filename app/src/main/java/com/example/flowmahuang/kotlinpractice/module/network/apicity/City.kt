package com.example.flowmahuang.kotlinpractice.module.network.apicity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class City {
    @SerializedName("id")
    @Expose
    private var id: String = ""
    @SerializedName("name")
    @Expose
    private var name: String = ""
    @SerializedName("towns")
    @Expose
    private var towns: List<Town>? = null

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getTowns(): List<Town>? {
        return towns
    }

    fun setTowns(towns: List<Town>) {
        this.towns = towns
    }
}