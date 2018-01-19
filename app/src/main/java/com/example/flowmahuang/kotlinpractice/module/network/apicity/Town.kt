package com.example.flowmahuang.kotlinpractice.module.network.apicity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by flowmahuang on 2017/12/25.
 */
class Town {
    @SerializedName("id")
    @Expose
    private var id: String = ""
    @SerializedName("name")
    @Expose
    private var name: String = ""

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
}