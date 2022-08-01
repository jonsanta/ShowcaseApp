package com.example.showcaseApp.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.showcaseApp.*
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //Creamos un objeto de MediaPlayer y por constructor pasamos el sonido que queremos que reproduzca
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.cancion)
        var position = 0 //variable que guardara la posicion de la reproduccion en milisegundos

        loadApp()

        viewBinding.acmImagebtnPlay.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if (!mp.isPlaying) { //Si se pulsa el botón start y no se está reproduciendo
                mp.seekTo(position)//Se empezará a reproducir desde la posición pasada por parámetro
                mp.start()//Inicia la reproducción
            }
        }

        viewBinding.acmImagebtnPause.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if (mp.isPlaying) //Si se pulsa el botón pause y se está reproduciendo
            {
                position = mp.currentPosition//Guardamos la posición actual de la canción
                mp.pause()//Pausamos la reproducción
            }
        }

        mp.setOnCompletionListener {
            position = 0 //Si la canción ha terminado la posición será restablecida a 0
        }

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