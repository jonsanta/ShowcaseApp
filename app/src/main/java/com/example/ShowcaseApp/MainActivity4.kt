package com.example.showcaseApp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        if(checkPermissions()) cargarGaleria() //Si tenemos los permisos de lectura abrimos la galería
        else//Si no tenemos permisos de lectura los pedimos
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        findViewById<Button>(R.id.btn_volver4).setOnClickListener{
            finish()//Cerramos la ventana y volvemos al MainActivity
            views.clear()
            selectedPhotos.clear()
            setEditMode(false, this)
        }

        findViewById<TextView>(R.id.discard).setOnClickListener(){
            selectedPhotos.clear()
            setEditMode(false, this)
        }

        findViewById<TextView>(R.id.remove).setOnClickListener(){
            val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()

            if(selectedPhotos.isNotEmpty())
                for(item in selectedPhotos){
                    bitmaps.remove(item)
                    landBitmaps.remove(item)
                    views.remove(item)
                    for(x in directorio)
                    {
                        if(x.name == item) x.delete()
                    }
                }
            selectedPhotos.clear()
            recyclerView.adapter?.notifyDataSetChanged()
            countSelectedPhotos(this)
        }
    }

    private fun cargarGaleria() //Cargar en un Thread secundario
    {
        //Lista que contiene todas las imágenes capturadas con la aplicación
        recyclerView = findViewById(R.id.galeria)
        var land = false
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = LinearLayoutManager(this)
        else{
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            land = true
        }
        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio != null)
            if(directorio.size > bitmaps.size){
                val activity : MainActivity4 = this
                lifecycleScope.launch{
                    recyclerView.adapter = ImagesAdapter(withContext(Dispatchers.IO){tarea(directorio, land)}, activity)
                }
            }
            else recyclerView.adapter = ImagesAdapter(getBitmaps(land), this)

        setEditMode(editMode, this)
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
        private val bitmaps = mutableMapOf<String, Bitmap>()
        private val landBitmaps = mutableMapOf<String, Bitmap>()
        private val views = mutableMapOf<String, ImagesAdapter.ViewHolder>()
        private val selectedPhotos = mutableSetOf<String>()
        private var editMode = false

        fun tarea(directorio : Array<File>, land: Boolean) : Map<String, Bitmap>{
            for(x in bitmaps.size until directorio.size)
            {
                val file = directorio.get(x)
                val fileInputStream = FileInputStream(file) //FileInputStream al que pasamos la imagen
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565 //Reducimos el peso de la imagen
                val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)//Generamos el bitmap
                if (bitmap != null) {
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, Resources.getSystem().displayMetrics.widthPixels, 1500, true)
                    val landBitmap = Bitmap.createScaledBitmap(bitmap, 730, 800, true)
                    bitmaps.put(file.name, resizedBitmap) //List for RecyclerView
                    landBitmaps.put(file.name, landBitmap)
                }
            }
            return getBitmaps(land)
        }
        
        //NEED REFACTOR

        fun getBitmaps(land: Boolean) : Map<String, Bitmap>{
            if(!land) return bitmaps
            else return landBitmaps
        }

        fun setViews(name: String, holder: ImagesAdapter.ViewHolder){
            views.put(name, holder)
        }

        fun countSelectedPhotos(activity: MainActivity4){
            val textview = activity.findViewById<TextView>(R.id.selectText)

            if(selectedPhotos.size == 0) textview.text = "Nada seleccionado"
            else if(selectedPhotos.size > 1) textview.text = "${selectedPhotos.size} elementos seleccionados"
            else textview.text = "${selectedPhotos.size} elemento seleccionado"
        }

        fun setSelectedPhotos(photo: String, activity: MainActivity4){
            if(selectedPhotos.contains(photo)) selectedPhotos.remove(photo)
            else selectedPhotos.add(photo)

            for(item in selectedPhotos) System.out.println(item)

            countSelectedPhotos(activity)
        }

        fun getSelectedPhotos() : MutableSet<String>{
            return selectedPhotos
        }

        fun isEditMode() : Boolean{
            return editMode
        }

        fun setViewVisibility(view : View, flag: Boolean) {
            if(flag){
                view.visibility = View.VISIBLE
                view.isEnabled = true
            }
            else{
                view.visibility = View.INVISIBLE
                view.isEnabled = false
            }
        }

        fun setEditMode(flag : Boolean, activity : MainActivity4){
            editMode = flag
            for(item in views) item.value.checkBox.isVisible = flag

            setViewVisibility(activity.findViewById<TextView>(R.id.selectText), flag)
            setViewVisibility(activity.findViewById<TextView>(R.id.discard), flag)
            setViewVisibility(activity.findViewById<TextView>(R.id.remove), flag)

            if(!flag)
                for(item in views) item.value.checkBox.isChecked = false

            countSelectedPhotos(activity)
        }
    }
}
