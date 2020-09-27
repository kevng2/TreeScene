package com.kevng2.treear

import android.app.Activity
import android.content.Intent
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
import okhttp3.ResponseBody
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import retrofit2.*
import java.lang.reflect.Type

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
                if (response.isSuccessful) {
                    val assets: ArrayList<Assets>? = response.body()?.assets
                    mPhotoRecyclerView = photo_recycler_view
                    mPhotoRecyclerView.layoutManager = GridLayoutManager(
                        this@SearchActivity, 3
                    )
                    mPhotoRecyclerView.adapter = PhotoAdapter(assets)
                }
            }
        })
    }

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val mPolyThumbnail: ImageView = itemView as ImageView
        private lateinit var mAsset: Assets

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(asset: Assets) {
            mAsset = asset
            Glide
                .with(this@SearchActivity)
                .load(asset.thumbnail.url)
                .into(mPolyThumbnail)
        }

        override fun onClick(v: View?) {
            for (format in mAsset.formats) {
                if (format.formatType == "GLTF2") {
                    val intent = Intent()
                    intent.putExtra("URL_VALUE", format.root.url)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    inner class PhotoAdapter(photos: ArrayList<Assets>?) : RecyclerView.Adapter<PhotoHolder>() {
        private val photosList: ArrayList<Assets>? = photos

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            LayoutInflater.from(this@SearchActivity)
                .inflate(R.layout.activity_search, parent, false)
            return PhotoHolder(ImageView(this@SearchActivity))
        }

        override fun getItemCount(): Int = photosList!!.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(photosList?.get(position)!!)
        }
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }
}
