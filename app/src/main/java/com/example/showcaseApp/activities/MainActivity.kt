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

        //Abre el Activity 2 - Contactos
        viewBinding.acmBtnBd.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        //Abre el Activity 3 - Camara
        viewBinding.acmBtnCamara.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Toast.makeText(this, "Accediendo a la Camara", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        //Abre el Activity 4 - Galería
        viewBinding.acmBtnGaleria.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadApp(){
        File("${getExternalFilesDir(null)}/images/thumbnails").mkdirs()
        val tempFolder  = File("${getExternalFilesDir(null)}/temp")
        if(tempFolder.exists())
            tempFolder.listFiles()?.forEach { it.delete()}
        else
            tempFolder.mkdir()
    }
}