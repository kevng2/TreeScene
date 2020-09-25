package com.kevng2.treear.api.assets.format.resources

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Resources {
    @SerializedName("url")
    @Expose
    lateinit var url: String

    @SerializedName("relativePath")
    @Expose
    lateinit var relativePath: String

    override fun toString(): String {
        return "Resources(url='$url', relativePath='$relativePath')"
    }
}
