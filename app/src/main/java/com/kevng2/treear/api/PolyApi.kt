package com.kevng2.treear.api

import com.kevng2.treear.BuildConfig
import retrofit2.Call
import retrofit2.http.GET

interface PolyApi {
    @GET(
       "v1/assets?keywords=cats&key=${BuildConfig.POLY_API_KEY}"
    )
    fun fetchModels(): Call<String>
}