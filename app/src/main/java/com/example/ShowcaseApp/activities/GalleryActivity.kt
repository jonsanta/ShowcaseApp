package com.example.showcaseApp.activities

import android.Manifest
import android.content.Context
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
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.GalleryActivityBinding
import com.example.showcaseApp.fragments.PhotoPreviewFragment

class GalleryActivity : AppCompatActivity(){
    private lateinit var viewBinding : GalleryActivityBinding

    private val READ_REQUEST_CODE = 123
    private var photoPreviewFragment : PhotoPreviewFragment? = null

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

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadGallery() //Permissions granted - Load Images
        else//Permissions denied
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(){
        if(photoPreviewFragment == null){
            Gallery.clearSelected()
            if(!Gallery.isEditMode()) {
                finish()
            }
            Gallery.setEditMode(false, this)
        }else{
            Gallery.setViewVisibility(findViewById<ImageButton>(R.id.ac4_remove), false)
            supportFragmentManager.popBackStack()
            fragmentClosed()
        }
    }

    fun fragmentClosed() {
        viewBinding.ac4BtnDiscard.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            onBackPressed()
        }

        viewBinding.ac4Remove.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            removePhoto()
        }
        photoPreviewFragment = null
        Gallery.clearSelected()
    }

    fun removePhoto(){
        val minValue = Gallery.getSelected().last()
        Gallery.getSelected().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it)
        }
        Gallery.removePhotos(this)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue,Gallery.getPhotos().size)
        Gallery.setEditMode(false, this)
    }

    fun setPhotoPreviewFragment(photoPreviewFragment: PhotoPreviewFragment){
        this.photoPreviewFragment = photoPreviewFragment
    }
}
