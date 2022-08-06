package com.example.showcaseApp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        loadApp()

        //Load Activity 2 - Contact Listing
        viewBinding.acmBtnBd.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        //Load Activity 3 - Camera
        viewBinding.acmBtnCamara.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Toast.makeText(this, "Accediendo a la Camara", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        //Load Activity 4 - Gallery
        viewBinding.acmBtnGaleria.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    //Create app folder structure & delete old temp files
    private fun loadApp(){
        File("${getExternalFilesDir(null)}/images/thumbnails").mkdirs()
        val tempFolder  = File("${getExternalFilesDir(null)}/temp")
        if(tempFolder.exists())
            tempFolder.listFiles()?.forEach { it.delete()}
        else
            tempFolder.mkdir()
    }
}