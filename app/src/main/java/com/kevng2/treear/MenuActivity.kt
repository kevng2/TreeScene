package com.kevng2.treear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        Glide.with(this)
            .load(R.drawable.tree)
            .into(background_image)

        start_button.setOnClickListener {
            val intent = Intent(this@MenuActivity, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}
