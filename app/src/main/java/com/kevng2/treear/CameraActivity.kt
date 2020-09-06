package com.kevng2.treear
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var mModelId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setModel(mModelId)
        mArFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
        setUpPlane()

        val treeList = arrayListOf("Oak", "Pine", "Elm", "Palm", "Cherry Blossom")
        val arrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            treeList
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tree_selection_spinner.adapter = arrayAdapter
        tree_selection_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setModel(position)
            }
        }
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
            .setSource(applicationContext, mModelId)
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
        setUpModel()
    }
}
