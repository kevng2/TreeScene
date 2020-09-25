package com.kevng2.treear

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.NodeParent
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private var mArFragment: ArFragment? = null
    private var isInsertMode: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setSupportActionBar(camera_toolbar)
        mArFragment = fragment as ArFragment
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setUpModel()
        setUpPlane()
        take_picture_button.setOnClickListener { takePhoto() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_camera, menu)

        val searchItem = menu?.findItem(R.id.tree_search_bar)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@CameraActivity, SearchActivity::class.java)
                intent.putExtra(SEARCH_QUERY, query)
                startActivityForResult(intent, REQUEST_MODEL_URL)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("CameraActivity", "onActivityResult (line 67): ")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_MODEL_URL) {
                Log.d(
                    "CameraActivity",
                    "onActivityResult (line 67): ${data?.getStringExtra("URL_VALUE")}"
                )
                data?.getStringExtra("URL_VALUE")?.let { setUpApiModel(it) }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.trash -> {
                if (isInsertMode) {
                    item.setIcon(R.drawable.ic_done)
                } else {
                    item.setIcon(R.drawable.ic_delete)
                }

                isInsertMode = !isInsertMode
            }
            R.id.help -> {
                AlertDialog.Builder(this@CameraActivity)
                    .setPositiveButton(android.R.string.ok) { dialog, which -> }
                    .setMessage(
                        "Welcome to TreeVisualizer\n\n" +
                                "To start, wave your camera around like the animation is indicating on " +
                                "the camera preview. Then, an indicator will pop up to plant some trees!\n\n" +
                                "Tap on any dotted area to plant a tree\n\n" +
                                "You can switch trees anytime by selecting the trees at the bottom\n\n" +
                                "Trash Icon: Delete mode. Tap on tree to delete\n\n" +
                                "Check mark Icon: Switch back to add mode\n\n" +
                                "Pinch to grow the tree\n\n" +
                                "Hold one finger on the tree and use another finger to rotate the tree\n\n"
                    )
                    .create()
                    .show()
            }
        }
        return true
    }

    private fun setUpPlane() {
        mArFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (isInsertMode) {
                val anchor: Anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(mArFragment?.arSceneView?.scene as NodeParent)
                createModel(anchorNode)
            }
        }
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

    private fun setUpApiModel(apiModel: String) {
        val model: Uri = Uri.parse("https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf")
        Log.d("CameraActivity", "setUpApiModel (line 155): $model")
        ModelRenderable.builder()
            .setSource(
                applicationContext, RenderableSource.builder().setSource(
                    applicationContext,
                    model,
                    RenderableSource.SourceType.GLTF2
                ).build()
            )
            .setRegistryId(model)
            .build()
            .thenAccept { t: ModelRenderable? -> mModelRenderable = t
                Log.d("CameraActivity", "setUpApiModel (line 167): $t")
            }
            .exceptionally { t: Throwable? ->
                Log.e("CameraActivity", "Could not fetch model from $model", t)
                return@exceptionally null
            }
    }

    private fun generateFilename(): String {
        val date: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
            Date()
        )
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + File.separator + "Sceneform/" + date + "_screenshot.jpg"
    }

    private fun saveBitmapToDisk(bitmap: Bitmap, filename: String) {
        val out = File(filename)
        if (!out.parentFile?.exists()!!) {
            out.parentFile?.mkdirs()
        }
        val outputStream = FileOutputStream(filename)
        val outputData = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData)
        outputData.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun takePhoto() {
        val filename = generateFilename()
        val frag = fragment as ArFragment
        val view = frag.arSceneView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()

        PixelCopy.request(view, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                saveBitmapToDisk(bitmap, filename)
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Photo saved",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Open in Photos") { v ->
                    val photoFile = File(filename)
                    val photoUri = FileProvider.getUriForFile(
                        this@CameraActivity,
                        "$packageName.ar.codelab.name.provider", photoFile
                    )
                    intent = Intent(Intent.ACTION_VIEW, photoUri)
                    intent.setDataAndType(photoUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
                snackbar.show()
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

    companion object {
        var mModelId: Int = R.raw.oak_tree
        var mModelRenderable: ModelRenderable? = null
        var SEARCH_QUERY: String = "SEARCH_QUERY"
        const val REQUEST_MODEL_URL = 0
        private const val TAG = "CameraActivity"
    }
}
