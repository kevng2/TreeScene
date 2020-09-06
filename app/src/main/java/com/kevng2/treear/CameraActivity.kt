package com.kevng2.treear
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.NodeParent
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class CameraActivity : AppCompatActivity() {
    private var mArFragment: ArFragment? = null
    private var mModelRenderable: ModelRenderable? = null
    private var mModelId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mArFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
        setModel(intent.getIntExtra(MenuActivity.SEND_TREE, 0))
        setUpModel()
        setUpPlane()
    }

    private fun setUpPlane() {
        mArFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor: Anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(mArFragment!!.arSceneView.scene as NodeParent)
            createModel(anchorNode)
        }
    }

    private fun createModel(anchorNode: AnchorNode) {
        val node = TransformableNode(mArFragment?.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = mModelRenderable
        node.select()
    }

    private fun setUpModel() {
        ModelRenderable.builder()
            .setSource(applicationContext, mModelId!!)
            .build()
            .thenAccept { renderable: ModelRenderable ->  mModelRenderable = renderable }
            .exceptionally { Toast.makeText(applicationContext, "Model can't be loaded",
                Toast.LENGTH_SHORT).show(); return@exceptionally null }
    }

    private fun setModel(treeId: Int) {
        when(treeId) {
            0 -> mModelId = R.raw.oak_tree
            1 -> mModelId = R.raw.model
            2 -> mModelId = R.raw.elm_tree
            3 -> mModelId = R.raw.queen_palm_tree
            4 -> mModelId = R.raw.cherry_blossom
        }
    }
}
