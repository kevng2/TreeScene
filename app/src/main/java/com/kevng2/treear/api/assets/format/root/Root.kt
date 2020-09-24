package com.kevng2.treear.api.assets.format.root

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Root {
    @SerializedName("url")
    @Expose
    lateinit var url: String

    @SerializedName("relativePath")
    @Expose
    lateinit var relativePath: String

    override fun toString(): String {
        return "Root(url='$url', relativePath='$relativePath')"
    }
}