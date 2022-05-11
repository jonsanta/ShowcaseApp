package com.example.ShowcaseApp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream

class CargarImagenHilo(var activity4: MainActivity4, var list: List<File>) : Thread() {
    override fun run() {//Se ejecutará cuando el hilo sea iniciado start()
        for(x in list.indices) {//Por cada imagen en la lista
            //Convertiremos los archivos físicos en Bitmaps(estos son manejados de manera mucho más eficiente por el dispositivo)
            val fileInputStream = FileInputStream(list.get(x)) //FileInputStream al que pasamos la imagen
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565 //Reducimos un poco la calidad de dicha imagen
            val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)//Generamos el bitmap
            if (bitmap != null) {
                MainActivity4.cargarImagenes(activity4, bitmap) //Pasamos el bitmap generado al método que se encargara de mostrarla en el activity
            }
        }
    }
}