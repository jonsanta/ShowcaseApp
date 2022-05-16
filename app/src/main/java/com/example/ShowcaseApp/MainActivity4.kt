package com.example.ShowcaseApp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream


class MainActivity4 : AppCompatActivity() {
    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        if(checkPermissions()) cargarGaleria() //Si tenemos los permisos de lectura abrimos la galería
        else//Si no tenemos permisos de lectura los pedimos
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        findViewById<Button>(R.id.btn_volver4).setOnClickListener{
            finish()//Cerramos la ventana y volvemos al MainActivity
        }
    }

    private fun cargarGaleria() //Cargar en un Thread secundario
    {
        //Lista que contiene todas las imágenes capturadas con la aplicación
        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio.size > bitmaps.size)
        {
            lifecycleScope.launch{
                withContext(Dispatchers.IO){tarea(directorio)}
            }
        }
        val recyclerview = findViewById<RecyclerView>(R.id.galeria)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val adapter = ListAdapter(bitmaps)

        recyclerview.adapter = adapter
    }

    //Función para manejar runOnUiThread (Es necesaria su ejecución en el Hilo que maneja la interfaz del activity)
    fun cargarImagenes(bitmap: Bitmap){
        runOnUiThread(Runnable {
            val imageView = ImageView(this)//Generamos un nuevo ImageView que contendrá una imagen
            imageView.setImageBitmap(bitmap)//Asignamos el bitmap recibido al ImageView creado

            //Parámetros para Formatear el diseño de la galería vertical
            imageView.adjustViewBounds = true
            val marginParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            marginParams.setMargins(0, 0, 0, 20)
            marginParams.gravity = Gravity.CENTER
            imageView.layoutParams = marginParams

            //Si el dispositivo se encuentra en modo vertical, asignaremos la imagen al LinearLayout existente
            /*
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (findViewById<LinearLayout>(R.id.LinearLayoutImagenes) != null)
                    findViewById<LinearLayout>(R.id.LinearLayoutImagenes).addView(imageView)
            }//Si el dispositivo se encuentra en modo horizontal, asignaremos la imagen al GridLayout existente
            else if(findViewById<GridLayout>(R.id.GridLayoutImagenes) != null) {
                //Redimensionaremos los bitmap recibidos
                val resized = Bitmap.createScaledBitmap(bitmap, 730, 500, true)
                //Creamos el ImageView
                val imageViewResized = ImageView(this)
                imageViewResized.setImageBitmap(resized)
                imageViewResized.adjustViewBounds = true
                findViewById<GridLayout>(R.id.GridLayoutImagenes).addView(imageViewResized)
            }
            */
        })
    }

    private fun checkPermissions() : Boolean //Devuelve true si los permisos están concedidos, de lo contrario false
    {
        return (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            cargarGaleria() //La aplicación dispone de los permisos del dispositivo
        else//Los permisos no han sido concedidos
            Toast.makeText(this, "No se han concedido los permisos", Toast.LENGTH_SHORT).show()
    }

    companion object{
        val bitmaps = mutableListOf<Bitmap>()
        fun tarea(directorio : Array<File>){
            for(x in directorio)
            {
                val fileInputStream = FileInputStream(x) //FileInputStream al que pasamos la imagen
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565 //Reducimos un poco la calidad de dicha imagen
                val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)//Generamos el bitmap
                if (bitmap != null) {
                    bitmaps.add(bitmap) //List for RecyclerView
                }
            }
        }
    }
}