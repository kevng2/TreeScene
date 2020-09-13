package com.kevng2.treear

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlinx.android.synthetic.main.gallery_images.*
import java.lang.reflect.Type

class GalleryFragment : Fragment() {
    private val mTreeImages = arrayListOf(
        R.drawable.oak,
        R.drawable.pine,
        R.drawable.elm,
        R.drawable.palm,
        R.drawable.cherry_blossom
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.gallery_fragment, container, false)
        val recyclerView = v.findViewById<RecyclerView>(R.id.gallery_recycler_view)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = GalleryAdapter(mTreeImages)
        return v
    }

    inner class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mImageButton: ImageButton? = null

        init {
            mImageButton = itemView as ImageButton
        }

        fun bind(treeId: Int) {
            mImageButton?.scaleType = ImageView.ScaleType.FIT_CENTER
            mImageButton?.setImageResource(treeId)
            mImageButton?.setOnClickListener {
                when (treeId) {
                    R.drawable.oak -> CameraActivity.mModelId = R.raw.oak_tree
                    R.drawable.elm -> CameraActivity.mModelId = R.raw.elm_tree
                    R.drawable.pine -> CameraActivity.mModelId = R.raw.model
                    R.drawable.palm -> CameraActivity.mModelId = R.raw.queen_palm_tree
                    R.drawable.cherry_blossom -> CameraActivity.mModelId = R.raw.cherry_blossom
                }
                setUpModel()
            }
        }
    }

    inner class GalleryAdapter(private val galleryList: ArrayList<Int>) :
        RecyclerView.Adapter<GalleryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
            //val inflater = LayoutInflater.from(parent.context)
            return GalleryHolder(ImageButton(activity))
        }

        override fun getItemCount(): Int = galleryList.size

        override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
            holder.bind(galleryList[position])
        }
    }

    private fun setUpModel() {
        ModelRenderable.builder()
            .setSource(activity, CameraActivity.mModelId)
            .build()
            .thenAccept { renderable: ModelRenderable ->
                CameraActivity.mModelRenderable = renderable
            }
            .exceptionally {
                Toast.makeText(
                    activity, "Model can't be loaded",
                    Toast.LENGTH_SHORT
                ).show(); return@exceptionally null
            }
    }
}