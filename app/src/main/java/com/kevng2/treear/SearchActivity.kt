package com.kevng2.treear

import android.annotation.SuppressLint
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
import com.github.kittinunf.fuel.httpDownload
import com.kevng2.treear.api.FileDownloadClient
import com.kevng2.treear.api.PolyApi
import com.kevng2.treear.api.Post
import com.kevng2.treear.api.assets.Assets
import com.kevng2.treear.api.assets.format.Format
import com.techyourchance.threadposter.BackgroundThreadPoster
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.ResponseBody
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import retrofit2.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class SearchActivity : AppCompatActivity() {
    private lateinit var mPhotoRecyclerView: RecyclerView
    private val entries: ArrayList<Entry> = ArrayList()

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
                    for (asset in assets!!) {
                        Log.d(
                            "SearchActivity",
                            "onResponse (line 52): ${asset.formats[0].root.url}"
                        )
                    }
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
            var foundObjFormat: Boolean = false
            Log.d("PhotoHolder", "onClick (line 83): ${mAsset.name}")
            for (format in mAsset.formats) {
                if (format.formatType == "GLTF") {
                    Log.d("PhotoHolder", "onClick (line 97): ${format.root.url}")
                    val intent = Intent()
                    intent.putExtra("URL_VALUE", format.root.url)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    /*
                    format.root.url.httpDownload().destination { response, url ->
                        File(filesDir, "asset.obj")
                    }.response { request, response, result ->
                        result.fold({}, {
                            Log.e("PhotoHolder", "An error occurred")
                        })
                    }
                    entries.add(Entry(format.root.relativePath, format.root.url))
                    requestDataFiles(mAsset, format)
                    foundObjFormat = true
                    break
                     */
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun requestDataFiles(asset: Assets, format: Format) {
        /*
        val retrofitGet: Retrofit = Retrofit.Builder()
            .baseUrl("https://poly.googleapis.com/")
            .build()

        val fileDownloadClient: FileDownloadClient =
            retrofitGet.create(FileDownloadClient::class.java)

        val call: Call<ResponseBody> = fileDownloadClient.downloadFile(asset.name)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SearchActivity", "onFailure (line 111): ", t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("SearchActivity", "onResponse (line 115): ${response.body()}")
            }
        })
         */

        for (resource in format.resources) {
            if (resource.relativePath.toLowerCase()
                    .endsWith(".mtl") || resource.url.toLowerCase().endsWith(".png")
            ) {
                resource.url.httpDownload().destination { response, url ->
                    File(filesDir, resource.relativePath)
                }.response { request, response, result ->
                    result.fold({}, {
                        Log.e("SearchActivity", "An error occurred ")
                    })
                }
                entries.add(Entry(resource.relativePath, resource.url))
            }
        }
//        downloadFiles()
    }

    /*
    private fun downloadFiles() {
        val backgroundThread = BackgroundThreadPoster()
        backgroundThread.post {
            for (entry in entries) {
                val url = URL(entry.mUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val responseCode = connection.responseCode
                if(responseCode != 200) {
                    Log.e("SearchActivity", "downloadFiles (line 142): cannot download file")
                }
                val outputStream = ByteArrayOutputStream()
                copyStream(connection.inputStream, outputStream)
                entry.mContents = outputStream.toByteArray()
            }
            importDownloadedObject()
        }
    }
     */

    private fun importDownloadedObject() {
    }

    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer: ByteArray = ByteArray(16384)
        var totalBytes = 0
        var bytesReadThisTime: Int = inputStream.read(buffer, 0, buffer.size)
        while (bytesReadThisTime > 0) {
            outputStream.write(buffer, 0, bytesReadThisTime)
            totalBytes += bytesReadThisTime
            bytesReadThisTime = inputStream.read(buffer, 0, buffer.size)
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

    companion object {
        class Entry(filename: String, url: String) {
            val mFileName: String = filename
            val mUrl: String = url
            lateinit var mContents: ByteArray
        }
    }
}

