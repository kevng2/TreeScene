package com.kevng2.treear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val intent = Intent(this, CameraActivity::class.java)
        start_button.setOnClickListener {
            startActivity(intent)
        }

        val treeList = arrayListOf("Oak", "Pine", "Elm", "Palm", "Cherry Blossom")
        val arrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            treeList
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tree_spinner.adapter = arrayAdapter
        tree_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                intent.putExtra(SEND_TREE, position)
            }
        }
    }

    companion object {
        const val SEND_TREE = "send_tree"
    }
}
