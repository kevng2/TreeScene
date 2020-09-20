package com.kevng2.treear.api

import com.kevng2.treear.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface PolyApi {
    @Headers("Content-Type: application/json")
    @GET(
       "v1/assets?" +
               "keywords=tree" +
               "&format=OBJ" +
               "&pageSize=10" +
               "&key=${BuildConfig.POLY_API_KEY}"
    )
    fun fetchModels(): Call<Post>
}