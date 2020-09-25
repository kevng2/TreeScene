package com.kevng2.treear.api

import com.kevng2.treear.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FileDownloadClient {
    @GET("v1/"
            + BuildConfig.POLY_API_KEY)
    fun downloadFile(@Query("name") name: String): Call<ResponseBody>
}