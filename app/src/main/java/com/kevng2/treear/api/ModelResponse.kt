package com.kevng2.treear.api

import com.google.gson.annotations.SerializedName
import com.kevng2.treear.PolyItem

class ModelResponse {
    @SerializedName("formats")
    lateinit var polyItems: List<PolyItem>
}