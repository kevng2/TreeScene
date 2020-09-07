package com.kevng2.treear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setSupportActionBar(camera_toolbar)
        setModel(mModelId)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mArFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
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
            Log.d("CameraActivity", item.itemId.toString())
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

    private fun setModel(treeId: Int) {
        when (treeId) {
            0 -> mModelId = R.raw.oak_tree
            1 -> mModelId = R.raw.model
            2 -> mModelId = R.raw.elm_tree
            3 -> mModelId = R.raw.queen_palm_tree
            4 -> mModelId = R.raw.cherry_blossom
        }
        setUpModel()
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
