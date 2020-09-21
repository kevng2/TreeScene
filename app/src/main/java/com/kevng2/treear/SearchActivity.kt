package com.kevng2.treear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kevng2.treear.api.PolyApi
import com.kevng2.treear.api.Post
import com.kevng2.treear.api.assets.Assets
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://poly.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val polyApi = retrofit.create(PolyApi::class.java)
        val polyRequest: Call<Post> = polyApi.fetchModels()

        polyRequest.enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("PolyFetcher", "Problem reading JSON ", t)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("PolyFetcher", "Received: $response")
                val assets: ArrayList<Assets>? = response.body()?.assets
                for (asset in assets!!) {
                    Log.d("PolyFetcher", "onResponse (line 45): $asset")
                }
            }
        })
    }
}
