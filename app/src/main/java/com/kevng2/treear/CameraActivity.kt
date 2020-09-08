package com.kevng2.treear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.NodeParent
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {
    private var mArFragment: ArFragment? = null
    private var mModelRenderable: ModelRenderable? = null
    private var mModelId: Int = R.raw.oak_tree
    private var isInsertMode: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setSupportActionBar(camera_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mArFragment = fragment as ArFragment
        setUpModel()
        setUpPlane()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_camera, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (isModel(item.itemId)) {
            when (item.itemId) {
                R.id.oak -> mModelId = R.raw.oak_tree
                R.id.pine -> mModelId = R.raw.model
                R.id.elm -> mModelId = R.raw.elm_tree
                R.id.palm -> mModelId = R.raw.queen_palm_tree
                R.id.cherry_blossom -> mModelId = R.raw.cherry_blossom
            }
            setUpModel()
        }

        when (item.itemId) {
            R.id.trash -> {
                isInsertMode = !isInsertMode
            }
        }
        return true
    }

    private fun setUpPlane() {
        Thread {
            mArFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                val anchor: Anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(mArFragment!!.arSceneView.scene as NodeParent)
                createModel(anchorNode)
            }
        }.start()
    }

    private fun createModel(anchorNode: AnchorNode) {
        val node = TransformableNode(mArFragment?.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = mModelRenderable
        node.setOnTapListener { hitTestResult, motionEvent ->
            if (!isInsertMode) {
                mArFragment?.arSceneView?.scene?.removeChild(anchorNode)
                anchorNode.anchor?.detach()
                anchorNode.setParent(null)
                anchorNode.renderable = null
            }
        }
        node.select()
    }

    private fun setUpModel() {
        ModelRenderable.builder()
            .setSource(applicationContext, mModelId)
            .build()
            .thenAccept { renderable: ModelRenderable -> mModelRenderable = renderable }
            .exceptionally {
                Toast.makeText(
                    applicationContext, "Model can't be loaded",
                    Toast.LENGTH_SHORT
                ).show(); return@exceptionally null
            }
    }

    private fun isModel(id: Int): Boolean {
        if (id == R.id.oak || id == R.id.pine || id == R.id.elm ||
            id == R.id.palm || id == R.id.cherry_blossom
        ) {
            return true
        }
        return false
    }
}
