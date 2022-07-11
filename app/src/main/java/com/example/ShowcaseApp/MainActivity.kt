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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //Fuerza modo Vertical para está actividad

        //Creamos un objeto de MediaPlayer y por constructor pasamos el sonido que queremos que reproduzca
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.cancion)
        var position = 0 //variable que guardara la posicion de la reproduccion en milisegundos

        //Loads gallery images
        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio!!.size > MainActivity4.getBitmaps(true).size){
          lifecycleScope.launch{ // Generate Bitmaps
                withContext(Dispatchers.IO){
                    for(i in directorio){
                        MainActivity4.setBitmap(i)
                        runOnUiThread{
                            if(isRecyclerViewInitialized())
                                recyclerView.adapter?.notifyDataSetChanged()
                                //recyclerView.adapter?.notifyItemInserted(MainActivity4.getBitmaps(true).size -1)
                        }
                    }
                }
            }
        }


        findViewById<ImageButton>(R.id.btn_start).setOnClickListener {
            if(!mp.isPlaying){ //Si se pulsa el botón start y no se está reproduciendo
                mp.seekTo(position)//Se empezará a reproducir desde la posición pasada por parámetro
                mp.start()//Inicia la reproducción
            }
        }

        findViewById<ImageButton>(R.id.btn_pause).setOnClickListener {
            if(mp.isPlaying) //Si se pulsa el botón pause y se está reproduciendo
            {
                position = mp.currentPosition//Guardamos la posición actual de la canción
                mp.pause()//Pausamos la reproducción
            }
        }

        mp.setOnCompletionListener {
            position = 0 //Si la canción ha terminado la posición será restablecida a 0
        }

        //Abre el Activity 2 - Base de datos
        findViewById<Button>(R.id.btn_bd).setOnClickListener {
            Toast.makeText(this, "Accediendo a la Base de Datos", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }

        //Abre el Activity 3 - Camara
        findViewById<Button>(R.id.btn_camara).setOnClickListener {
            Toast.makeText(this, "Entrando en Activity 3", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity3::class.java)
            startActivity(intent)
        }

        //Abre el Activity 4 - Galería
        findViewById<Button>(R.id.btn_galeria).setOnClickListener {
            Toast.makeText(this, "Accediendo a la Galería", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity4::class.java)
            startActivity(intent)
        }
    }

    //PROVISIONAL COMPANION OBJECT
    companion object{
        private lateinit var recyclerView: RecyclerView

        fun isRecyclerViewInitialized() : Boolean{
            return this::recyclerView.isInitialized
        }

        fun setRecyclerView(recyclerView: RecyclerView){
            this.recyclerView = recyclerView
        }
    }
}