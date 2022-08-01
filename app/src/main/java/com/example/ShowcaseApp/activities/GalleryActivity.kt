package com.example.showcaseApp.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.GridSpacingItemDecoration
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.GalleryActivityBinding

class GalleryActivity : AppCompatActivity(){
    private lateinit var viewBinding : GalleryActivityBinding
    var galleryActivityListener: GalleryActivityListener? = null

    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = GalleryActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if(checkPermissions())
            loadGallery()
        else//Ask for READING Permissions
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        viewBinding.ac4BtnDiscard.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            onBackPressed()
        }

        viewBinding.ac4Remove.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            removePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadGallery() //Permissions granted - Load Images
        else//Permissions denied
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(){
        if(galleryActivityListener == null) {
            if (!Gallery.isEditMode())
                finish()
            Gallery.setEditMode(false, this)
            Gallery.clearSelected()
        }
        else if (Gallery.getSelected().size > 0) {
            galleryActivityListener!!.onClickListener(Gallery.getSelectedPhotos()[0], 150)
        } else{
            findViewById<ImageView>(R.id.ac4_imagepreview).visibility = View.INVISIBLE
            galleryActivityListener = null
        }
    }

    fun removePhoto(){
        val minValue = Gallery.getSelected().last()
        Gallery.getSelected().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it)
        }
        Gallery.removePhotos(this)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue,Gallery.getPhotos().size)
        onBackPressed()
    }

    private fun loadGallery()
    {
        var recyclerView = viewBinding.ac4RecyclerView
        // LayoutManager depends on device orientation
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView = setRecyclerView(recyclerView, 3,  this)
        else
            recyclerView = setRecyclerView(recyclerView, 5,  this)

        Gallery.setPhotos(this)

        recyclerView.adapter = GalleryAdapter(Gallery.getPhotos(),this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        Gallery.setEditMode(Gallery.isEditMode(), this)
    }

    private fun setRecyclerView(recyclerView: RecyclerView, spanCount : Int, context : Context) : RecyclerView{
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, -10))
        return recyclerView
    }

    private fun checkPermissions() : Boolean //True: Permission Granted, False: Permission Denied
    {
        return (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    fun closeImage(){
        viewBinding.ac4BtnDiscard.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            onBackPressed()
        }
        viewBinding.ac4Remove.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            removePhoto()
        }
        Gallery.getSelectedPhotos().forEach{
            it.getView()!!.photo.alpha = 1f
        }
        Gallery.setEditMode(false, this)
        Gallery.clearSelected()
        findViewById<ImageView>(R.id.ac4_imagepreview).visibility = View.INVISIBLE
        galleryActivityListener = null
    }

    interface GalleryActivityListener{
        fun onClickListener(photo: Photo, duration : Int)
    }
}
