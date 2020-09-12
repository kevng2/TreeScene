package com.kevng2.treear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    class GalleryHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.gallery_images, parent, false)) {
        private var mTreeImage: ImageButton? = null

        init {
            mTreeImage = itemView.findViewById(R.id.tree_image_button)
        }

        fun bind(imageId: Int) {
            mTreeImage?.setImageResource(imageId)
        }
    }

    class GalleryAdapter(val galleryList: ArrayList<Int>) : RecyclerView.Adapter<GalleryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
            val inflater = LayoutInflater.from(parent.context)
            return GalleryHolder(inflater, parent)
        }

        override fun getItemCount(): Int = galleryList.size

        override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
            holder.bind(galleryList[position])
        }
    }
}