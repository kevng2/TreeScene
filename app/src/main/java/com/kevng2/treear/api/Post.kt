package com.kevng2.treear.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevng2.treear.api.assets.Assets

class Post {
    @SerializedName("assets")
    @Expose
    lateinit var assets: ArrayList<Assets>

    override fun toString(): String {
        return "Post(assets=$assets)"
    }
}
