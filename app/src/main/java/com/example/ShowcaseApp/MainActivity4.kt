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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        if(checkPermissions()) loadGallery()
        else//Ask for READING Permissions
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        findViewById<Button>(R.id.btn_volver4).setOnClickListener{
            views.clear()
            selectedImages.clear()
            setEditMode(false, this)
            finish()
        }

        findViewById<TextView>(R.id.discard).setOnClickListener(){
            selectedImages.clear()
            setEditMode(false, this)
        }

        findViewById<TextView>(R.id.remove).setOnClickListener(){
            val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()

            if(selectedImages.isNotEmpty())
                for(item in selectedImages){
                    bitmaps.remove(item)
                    landBitmaps.remove(item)
                    views.remove(item)
                    for(x in directorio!!)
                    {
                        if(x.name == item) x.delete()
                    }
                }
            selectedImages.clear()
            findViewById<RecyclerView>(R.id.galeria).adapter?.notifyDataSetChanged()
            countSelectedPhotos(this)
        }
    }

    private fun loadGallery()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.galeria)
        var land = false
        // LayoutManager depends on device orientation
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = LinearLayoutManager(this)
        else{
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            land = true
        }

        val directorio = File("${getExternalFilesDir(null)}/PacImagenes/").listFiles()
        if(directorio!!.size > bitmaps.size){
            val activity : MainActivity4 = this
            lifecycleScope.launch{ // Generate Bitmaps && populate RecyclerView
                recyclerView.adapter = ImagesAdapter(withContext(Dispatchers.IO){setBitmaps(directorio, land)}, activity)
            }
        }
        else // populate RecyclerView with existing bitmaps
            recyclerView.adapter = ImagesAdapter(getBitmaps(land), this)

        setEditMode(editMode, this)
    }

    private fun checkPermissions() : Boolean //True: Permission Granted, False: Permission Denied
    {
        return (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadGallery() //Permissions granted - Load Images
        else//Permissions denied
            Toast.makeText(this, "No se han concedido los permisos", Toast.LENGTH_SHORT).show()
    }

    companion object{
        private val bitmaps = mutableMapOf<String, Bitmap>() // Key: image name - value: portrait bitmap
        private val landBitmaps = mutableMapOf<String, Bitmap>()// Key: image name - value: landscape bitmap
        private val views = mutableMapOf<String, ImagesAdapter.ViewHolder>() // Contains: RecyclerView items
        private val selectedImages = mutableSetOf<String>() // Contains: map Keys = Image names

        private var editMode = false

        //Generates Bitmaps
        fun setBitmaps(directorio : Array<File>, land: Boolean) : Map<String, Bitmap>{
            for(x in bitmaps.size until directorio.size)
            {
                val file = directorio.get(x)
                val fileInputStream = FileInputStream(file)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)
                if (bitmap != null) {
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, Resources.getSystem().displayMetrics.widthPixels, 1500, true)
                    val landBitmap = Bitmap.createScaledBitmap(bitmap, 730, 800, true)
                    bitmaps.put(file.name, resizedBitmap) // Populate portrait bitmaps map --> For RecyclerView
                    landBitmaps.put(file.name, landBitmap) // Populate landscape bitmaps map --> For RecyclerView
                }
            }
            return getBitmaps(land)
        }

        fun getBitmaps(land: Boolean) : Map<String, Bitmap>{
            if(!land) return bitmaps
            else return landBitmaps
        }

        // Enables UI for editing mode
        fun setEditMode(flag : Boolean, activity : MainActivity4){
            for(item in views){
                item.value.checkBox.isVisible = flag
                if(!flag)
                    item.value.checkBox.isChecked = false
            }

            setViewVisibility(activity.findViewById<TextView>(R.id.selectText), flag)
            setViewVisibility(activity.findViewById<TextView>(R.id.discard), flag)
            setViewVisibility(activity.findViewById<TextView>(R.id.remove), flag)

            countSelectedPhotos(activity)
            editMode = flag
        }

        fun isEditMode() : Boolean{
            return editMode
        }

        fun setViews(name: String, holder: ImagesAdapter.ViewHolder){
            views.put(name, holder)
        }

        // Add-Remove from selection list
        fun setSelectedImages(photo: String, activity: MainActivity4){
            if(selectedImages.contains(photo)) selectedImages.remove(photo)
            else selectedImages.add(photo)

            for(item in selectedImages) System.out.println(item)

            countSelectedPhotos(activity)
        }

        fun getSelectedImages() : MutableSet<String>{
            return selectedImages
        }

        // Selection Text on EditMode
        fun countSelectedPhotos(activity: MainActivity4){
            val textview = activity.findViewById<TextView>(R.id.selectText)

            if(selectedImages.size == 0) textview.text = "Nada seleccionado"
            else if(selectedImages.size > 1) textview.text = "${selectedImages.size} elementos seleccionados"
            else textview.text = "${selectedImages.size} elemento seleccionado"
        }

        // Enable-Disable EditMode views (Top box & buttons)
        fun setViewVisibility(view : View, flag: Boolean) {
            view.isEnabled = flag
            if(flag)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.INVISIBLE
        }
    }
}
