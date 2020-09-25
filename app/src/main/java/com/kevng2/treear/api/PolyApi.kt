package com.kevng2.treear.api

import com.kevng2.treear.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PolyApi {
    @Headers("Content-Type: application/json")
    @GET(
       "v1/assets?" +
               "&format=GLTF" +
               "&pageSize=50" +
               "&key=${BuildConfig.POLY_API_KEY}"
    )
    fun fetchModels(@Query("keywords") keywords: String): Call<Post>
}