package com.example.flowmahuang.kotlinpractice.module.network.apiweather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by flowmahuang on 2018/1/3.
 */
class Special {
    @SerializedName("title")
    @Expose
    private var title: String? = null
    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("desc")
    @Expose
    private var desc: String? = null
    @SerializedName("at")
    @Expose
    private var at: String? = null
    @SerializedName("img")
    @Expose
    private var img: String? = null

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getDesc(): String? {
        return desc
    }

    fun setDesc(desc: String) {
        this.desc = desc
    }

    fun getAt(): String? {
        return at
    }

    fun setAt(at: String) {
        this.at = at
    }

    fun getImg(): String? {
        return img
    }

    fun setImg(img: String) {
        this.img = img
    }
}