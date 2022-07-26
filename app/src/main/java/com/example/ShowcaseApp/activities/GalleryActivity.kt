package com.example.showcaseApp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.R
import java.io.File

class GalleryActivity : AppCompatActivity() {
    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)

        if(checkPermissions()) loadGallery()
        else//Ask for READING Permissions
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        findViewById<ImageButton>(R.id.ac4_btn_discard).setOnClickListener{
            if(Gallery.isEditMode()) {
                Gallery.getSelectedImages().clear()
                Gallery.setEditMode(false, this)
            }else{
                Gallery.clearViews()
                Gallery.getSelectedImages().clear()
                Gallery.setEditMode(false, this)
                finish()
            }
        }

        findViewById<ImageButton>(R.id.ac4_remove).setOnClickListener{
            val directorio = File("${getExternalFilesDir(null)}/images/").listFiles()

            if(Gallery.getSelectedImages().isNotEmpty())
                for(item in Gallery.getSelectedImages()){
                    Gallery.removeBitmap(item)
                    for(x in directorio!!)
                    {
                        if(x.name == item) x.delete()
                    }
                }
            Gallery.getSelectedImages().clear()
            findViewById<RecyclerView>(R.id.ac4_recyclerView).adapter?.notifyDataSetChanged()
            Gallery.countSelectedPhotos(this)
        }
    }

    private fun loadGallery()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.ac4_recyclerView)
        var land = false
        // LayoutManager depends on device orientation
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = LinearLayoutManager(this)
        else{
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            land = true
        }
        //load images
        recyclerView.adapter = GalleryAdapter(Gallery.getBitmaps(land), this)

        //set MainActivity RecyclerView (REF) -- PROVISIONAL
        Gallery.setRecyclerView(recyclerView)


        Gallery.setEditMode(Gallery.isEditMode(), this)
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
        if(Gallery.isEditMode())
        {
            Gallery.getSelectedImages().clear()
            Gallery.setEditMode(false, this)
        }else
            finish()
    }
}
