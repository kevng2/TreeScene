package com.kevng2.treear

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kevng2.treear.api.PolyApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "PolyFetcher"

class PolyFetcher {
    private val polyApi: PolyApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://poly.googleapis.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        polyApi = retrofit.create(PolyApi::class.java)
    }

    fun fetchModels() : LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val polyRequest: Call<String> = polyApi.fetchModels()

        polyRequest.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("CameraActivity", "Failed to fetch photos ", t)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("CameraActivity", "Response received: ${response.body()} ")
                responseLiveData.value = response.body()
            }
        })

        return responseLiveData
    }

    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection

        val out = ByteArrayOutputStream()
        val inputStreamIn = connection.inputStream

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            Log.e(
                "PolyFetcher",
                "getUrlBytes (line 16): ${connection.responseMessage}: with $urlSpec"
            )
        }

        var bytesRead: Int
        val buffer = ByteArray(1024)
        bytesRead = inputStreamIn.read(buffer)

        while (bytesRead > 0) {
            out.write(buffer, 0, bytesRead)
            bytesRead = inputStreamIn.read(buffer)
        }
        out.close()
        connection.disconnect()
        return out.toByteArray()
    }

    fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }
}