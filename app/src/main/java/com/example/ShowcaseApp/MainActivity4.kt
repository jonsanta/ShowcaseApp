package com.example.ShowcaseApp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
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
        val recyclerview = findViewById<RecyclerView>(R.id.galeria)
        var land = false
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) recyclerview.layoutManager = LinearLayoutManager(this)
        else if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerview.layoutManager = GridLayoutManager(this, 3)
            land = true
        }

        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio != null && bitmaps != null)
        {
            if(directorio.size > bitmaps.size)
                lifecycleScope.launch{
                    recyclerview.adapter = ImagesAdapter(withContext(Dispatchers.IO){tarea(directorio, land)})
                }
            else if(!land) recyclerview.adapter = ImagesAdapter(bitmaps)
            else recyclerview.adapter = ImagesAdapter(landBitmaps)
        }
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
        val landBitmaps = mutableListOf<Bitmap>()
        fun tarea(directorio : Array<File>, land: Boolean) : List<Bitmap>{
            for(x in bitmaps.size until directorio.size)
            {
                val fileInputStream = FileInputStream(directorio.get(x)) //FileInputStream al que pasamos la imagen
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565 //Reducimos un poco la calidad de dicha imagen
                val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)//Generamos el bitmap
                if (bitmap != null) {
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, Resources.getSystem().getDisplayMetrics().widthPixels, 1500, true)
                    val landBitmap = Bitmap.createScaledBitmap(bitmap, 730, 800, true)
                    bitmaps.add(resizedBitmap) //List for RecyclerView
                    landBitmaps.add(landBitmap)
                }
            }
            if(!land) return bitmaps
            else return landBitmaps
        }
    }
}