package com.example.showcaseApp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //Fuerza modo Vertical para está actividad


        //Creamos un objeto de MediaPlayer y por constructor pasamos el sonido que queremos que reproduzca
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.cancion)
        var position = 0 //variable que guardara la posicion de la reproduccion en milisegundos

        //Loads gallery images
        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio!!.size > Gallery.getBitmaps(true).size){
          lifecycleScope.launch{ // Generate Bitmaps
                withContext(Dispatchers.IO){
                    for(i in directorio){
                        Gallery.setBitmap(i)
                        runOnUiThread{
                            if(Gallery.isRecyclerViewInitialized())
                                Gallery.getRecyclerView().adapter?.notifyDataSetChanged()
                                //recyclerView.adapter?.notifyItemInserted(MainActivity4.getBitmaps(true).size -1)
                        }
                    }
                }
            }
        }


        findViewById<ImageButton>(R.id.acm_imagebtn_play).setOnClickListener {
            if(!mp.isPlaying){ //Si se pulsa el botón start y no se está reproduciendo
                mp.seekTo(position)//Se empezará a reproducir desde la posición pasada por parámetro
                mp.start()//Inicia la reproducción
            }
        }

        findViewById<ImageButton>(R.id.acm_imagebtn_pause).setOnClickListener {
            if(mp.isPlaying) //Si se pulsa el botón pause y se está reproduciendo
            {
                position = mp.currentPosition//Guardamos la posición actual de la canción
                mp.pause()//Pausamos la reproducción
            }
        }

        mp.setOnCompletionListener {
            position = 0 //Si la canción ha terminado la posición será restablecida a 0
        }

        //Abre el Activity 2 - Contactos
        findViewById<Button>(R.id.acm_btn_bd).setOnClickListener {
            val intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }

        //Abre el Activity 3 - Camara
        findViewById<Button>(R.id.acm_btn_camara).setOnClickListener {
            Toast.makeText(this, "Accediendo a la Camara", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity3::class.java)
            startActivity(intent)
        }

        //Abre el Activity 4 - Galería
        findViewById<Button>(R.id.acm_btn_galeria).setOnClickListener {
            val intent = Intent(this,MainActivity4::class.java)
            startActivity(intent)
        }
    }
}