package com.kevng2.treear

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.util.Linkify
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class ApiCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_check)
        val s = SpannableString(
            "Please get an API key and " +
                    "paste it into the apikey.properties " +
                    "file inside of the Android Studio project directory"
        )
        Linkify.addLinks(s, Linkify.WEB_URLS)
        if (BuildConfig.POLY_API_KEY.contains("***")) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Poly API Key Not Found")
                .setMessage(s)
                .setPositiveButton("Get API key") { dialog, which ->
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://developers.google.com/poly/develop/api")
                    )
                    startActivity(intent)
                    finish()
                    exitProcess(0)
                }
                .create()
                .show()
        } else {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
