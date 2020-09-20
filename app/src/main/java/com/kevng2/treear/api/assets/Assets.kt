package com.kevng2.treear.api.assets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kevng2.treear.api.assets.format.Format

class Assets {
    @SerializedName("name")
    @Expose
    lateinit var name: String

    @SerializedName("displayName")
    @Expose
    lateinit var displayName: String

    @SerializedName("formats")
    @Expose
    lateinit var formats: ArrayList<Format>

    override fun toString(): String {
        return "Assets(name='$name', displayName='$displayName' formats=$formats)"
    }
}
