package com.kevng2.treear.api.assets.format

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Format {
    @SerializedName("formatType")
    @Expose
    lateinit var formatType: String

    override fun toString(): String {
        return "Format(formatType='$formatType')"
    }
}