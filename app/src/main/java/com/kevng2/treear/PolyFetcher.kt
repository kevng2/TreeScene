package com.kevng2.treear

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kevng2.treear.api.ModelResponse
import com.kevng2.treear.api.PolyApi
import com.kevng2.treear.api.PolyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "PolyFetcher"

class PolyFetcher {
    private val polyApi: PolyApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://poly.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        polyApi = retrofit.create(PolyApi::class.java)
    }

    fun fetchModels() : LiveData<List<PolyItem>> {
        val responseLiveData: MutableLiveData<List<PolyItem>> = MutableLiveData()
        val polyRequest: Call<PolyResponse> = polyApi.fetchModels()

        polyRequest.enqueue(object : Callback<PolyResponse> {
            override fun onFailure(call: Call<PolyResponse>, t: Throwable) {
                Log.e("CameraActivity", "Failed to fetch photos ", t)
            }

            override fun onResponse(call: Call<PolyResponse>, response: Response<PolyResponse>) {
                Log.d("PolyFetcher", "Response Received: ${response.body()}")
                val polyResponse: PolyResponse? = response.body()
                /*
                val modelResponse: ModelResponse? = polyResponse?.models
                var polyItems: List<PolyItem> = modelResponse?.polyItems ?: mutableListOf()
                polyItems = polyItems.filterNot {
                    it.name.isBlank()
                }
                responseLiveData.value = polyItems
                 */
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