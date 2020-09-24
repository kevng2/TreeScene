package com.kevng2.treear.api.assets.format

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevng2.treear.api.assets.format.root.Root
import org.json.JSONArray

class Format {
    @SerializedName("formatType")
    @Expose
    lateinit var formatType: String

    @SerializedName("root")
    @Expose
    lateinit var root: Root

    @SerializedName("resources")
    @Expose
    lateinit var resources: JsonArray

    override fun toString(): String {
        return "Format(formatType='$formatType', root=$root, resources=$resources)"
    }
}