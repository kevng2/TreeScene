package com.kevng2.treear.api.assets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Thumbnail {
    @SerializedName("url")
    @Expose
    lateinit var url: String

    override fun toString(): String {
        return "Thumbnail(url='$url')"
    }
}
