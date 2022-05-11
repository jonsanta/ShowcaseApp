package com.example.ShowcaseApp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import java.io.File


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
        val directorioImagenes = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        val nImagenes : Double = directorioImagenes.size.toDouble()/4 //Dividimos el número de imágenes totales entre 4
        //4 Hilos serán los encargados de cargar dichas imágenes en el activity y evitar perdidas de rendimiento o cargas lentas

        //Cada uno de los hilos se encargará del mismo número de imágenes
        val arr = intArrayOf(nImagenes.toInt(), nImagenes.toInt(), nImagenes.toInt(), nImagenes.toInt())

        //Evidentemente, cuando la división da un número con decimal buscaremos otra solución
        if(!nImagenes.rem(1).equals(0.0))
        {
            //Los 3 primeros Hilos manejarán solo el número entero
            val temp = nImagenes - nImagenes.toInt()
            arr[3] = nImagenes.toInt() + (temp * 4).toInt() //El cuarto hilo asumirá los decimales de los 3 primeros hilos
            //Ejemplo 11/4 = 2,75. Hilo 1 = 2 imágenes, Hilo 2 = 2 imágenes, Hilo 3 = 2 imágenes, Hilo 4 = 5 imágenes
            //De esta manera evitamos los números decimales
        }

        var indice = 0 //Variable auxiliar
        for(x in arr.indices) //Por cada Hilo que tenemos
        {
            val listaFiles = mutableListOf<File>() //Almacenaremos las imágenes que gestionara dicho hilo en una lista
            for(i in 0 until arr[x])//Rellenamos dicha lista
            {
                listaFiles.add(directorioImagenes.get(indice))
                indice++
            }
            val hilo = CargarImagenHilo(this, listaFiles)//Creamos el objeto Hilo
            hilo.start()//Iniciamos el hilo
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

    //Función estática para manejar runOnUiThread (Es necesaria su ejecución en el Hilo que maneja la interfaz del activity)
    companion object {
        fun cargarImagenes(activity4: MainActivity4, bitmap: Bitmap){
            activity4.runOnUiThread(Runnable {
                val imageView = ImageView(activity4)//Generamos un nuevo ImageView que contendrá una imagen
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
                if(activity4.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (activity4.findViewById<LinearLayout>(R.id.LinearLayoutImagenes) != null)
                        activity4.findViewById<LinearLayout>(R.id.LinearLayoutImagenes).addView(imageView)
                }//Si el dispositivo se encuentra en modo horizontal, asignaremos la imagen al GridLayout existente
                else if(activity4.findViewById<GridLayout>(R.id.GridLayoutImagenes) != null) {
                    //Redimensionaremos los bitmap recibidos
                    val resized = Bitmap.createScaledBitmap(bitmap, 730, 500, true)
                    //Creamos el ImageView
                    val imageViewResized = ImageView(activity4)
                    imageViewResized.setImageBitmap(resized)
                    imageViewResized.adjustViewBounds = true
                    activity4.findViewById<GridLayout>(R.id.GridLayoutImagenes).addView(imageViewResized)
                }
            })
        }
    }
}