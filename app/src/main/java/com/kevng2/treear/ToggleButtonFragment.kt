package com.kevng2.treear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlinx.android.synthetic.main.toggle_button.*

class ToggleButtonFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.toggle_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        oak.setOnClickListener {
            CameraActivity.mModelId = R.raw.oak_tree
            setUpModel()
        }

        pine.setOnClickListener {
            CameraActivity.mModelId = R.raw.model
            setUpModel()
        }

        elm.setOnClickListener {
            CameraActivity.mModelId = R.raw.elm_tree
            setUpModel()
        }

        palm.setOnClickListener {
            CameraActivity.mModelId = R.raw.queen_palm_tree
            setUpModel()
        }

        cherry_blossom.setOnClickListener {
            CameraActivity.mModelId = R.raw.cherry_blossom
            setUpModel()
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
