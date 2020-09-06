package com.kevng2.treear
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mArFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
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
            .setSource(this, R.raw.model)
            .build()
            .thenAccept { renderable: ModelRenderable ->  mModelRenderable = renderable }
            .exceptionally { Toast.makeText(this, "Model can't be loaded",
                Toast.LENGTH_SHORT).show(); return@exceptionally null }
    }
}
