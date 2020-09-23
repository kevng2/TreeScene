package com.kevng2.treear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kevng2.treear.api.PolyApi
import com.kevng2.treear.api.Post
import com.kevng2.treear.api.assets.Assets
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {
    private lateinit var mPhotoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://poly.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val polyApi = retrofit.create(PolyApi::class.java)
        val polyRequest: Call<Post> =
            polyApi.fetchModels(intent.getStringExtra(CameraActivity.SEARCH_QUERY) as String)

        polyRequest.enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("SearchActivity", "Problem reading JSON ", t)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("SearchActivity", "Received: $response")
                val assets: ArrayList<Assets>? = response.body()?.assets
                val url: ArrayList<String> = ArrayList()
                for (asset in assets!!) {
                    url.add(asset.thumbnail.url)
                }
                mPhotoRecyclerView = photo_recycler_view
                mPhotoRecyclerView.layoutManager = GridLayoutManager(this@SearchActivity, 3)
                mPhotoRecyclerView.adapter = PhotoAdapter(url)
            }
        })
    }

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mPolyThumbnail: ImageView = itemView as ImageView

        fun bind(url: String) {
            Glide
                .with(this@SearchActivity)
                .load(url)
                .into(mPolyThumbnail)
        }
    }

    inner class PhotoAdapter(photos: ArrayList<String>) : RecyclerView.Adapter<PhotoHolder>() {
        private val photosList: ArrayList<String> = photos

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            LayoutInflater.from(this@SearchActivity)
                .inflate(R.layout.activity_search, parent, false)
            return PhotoHolder(ImageView(this@SearchActivity))
        }

        override fun getItemCount(): Int = photosList.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(photosList[position])
        }
    }
}
