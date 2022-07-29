package com.example.showcaseApp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.GridSpacingItemDecoration
import com.example.showcaseApp.classes.Photo
import java.io.File

class GalleryActivity : AppCompatActivity() {
    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)

        if(checkPermissions())
            loadGallery()
        else//Ask for READING Permissions
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        findViewById<ImageButton>(R.id.ac4_btn_discard).setOnClickListener{
            onBackPressed()
        }

        findViewById<ImageButton>(R.id.ac4_remove).setOnClickListener{
            Gallery.removePhotos()
            Gallery.clearSelected()
            findViewById<RecyclerView>(R.id.ac4_recyclerView).adapter?.notifyDataSetChanged()
            Gallery.countSelectedPhotos(this)
        }
    }

    private fun loadGallery()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.ac4_recyclerView)
        // LayoutManager depends on device orientation
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(3, -10))
        }
        else {
            recyclerView.layoutManager = GridLayoutManager(this, 5)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(5, -10))
        }

        val directory = removeElement(File("${getExternalFilesDir(null)}/images/").listFiles()!!)
        val thumbnails = File("${getExternalFilesDir(null)}/images/thumbnails/").listFiles()



        //Loads gallery images
        if(thumbnails != null) {
            if (Gallery.photos.size != directory.size && !thumbnails.isEmpty()) {
                directory.forEachIndexed{index, file ->
                    Gallery.photos.add(Photo(file, thumbnails.get(index)))
                }
            }
        }

        recyclerView.adapter = GalleryAdapter(Gallery.photos,this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        Gallery.setEditMode(Gallery.isEditMode(), this)
    }

    private fun removeElement(arr : Array<File>) : Array<File>{
        val arrList = arr.toMutableList()

        arrList.removeAt(0)
        return arrList.toTypedArray()
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
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(){
        Gallery.clearSelected()
        Gallery.setEditMode(false, this)
        if(!Gallery.isEditMode()) {
            Gallery.removeViews()
            finish()
        }
    }
}
