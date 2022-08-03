package com.example.showcaseApp.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.adapters.ImageAdapter
import com.example.showcaseApp.classes.*
import com.example.showcaseApp.databinding.GalleryActivityBinding


class GalleryActivity : AppCompatActivity(), GalleryAdapter.GalleryListener{
    private lateinit var viewBinding : GalleryActivityBinding

    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = GalleryActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if(checkPermissions())
            loadGallery()
        else//Ask for READING Permissions
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        viewBinding.ac4BtnDiscard.setOnClickListener {
            Utils.preventTwoClick(it)
            onBackPressed()
        }

        viewBinding.ac4Remove.setOnClickListener {
            Utils.preventTwoClick(it)
            removePhoto()
        }

        val activity : GalleryActivity = this
        (viewBinding.ac4Imagepreview as ViewPager).addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewBinding.ac4RecyclerView.scrollToPosition(position)
                Gallery.clearSelected()
                Gallery.setSelected(Gallery.getPhotos()[position], position, activity)
                Gallery.getPhotos()[position].isExpanded(true)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadGallery() //Permissions granted - Load Images
        else//Permissions denied
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(){
        if(Gallery.isEditMode()){
            Gallery.setEditMode(false, this)
        }
        else if(Gallery.getSelected().isNotEmpty()){
            Gallery.setViewVisibility(findViewById(R.id.ac4_remove), false)
            GalleryAnimations().animate(Gallery.getPhotos()[(viewBinding.ac4Imagepreview as ViewPager).currentItem], viewBinding.ac4Imagepreview, viewBinding.ac4RecyclerView)
        }
        else{
            finish()
            Gallery.clearSelected()
        }
        Gallery.clearSelected()
    }

    override fun onLongItemClick(photo: Photo, position: Int) {
        if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
            Gallery.setEditMode(true, this)// Enables editMode
            select(photo, position)
        }
        else select(photo, position)
    }

    override fun onShortItemClick(photo: Photo, position: Int) {
        if(Gallery.isEditMode())
            select(photo, position)
        else {
            GalleryAnimations().animate(photo, viewBinding.ac4Imagepreview, viewBinding.ac4RecyclerView)
            Gallery.setViewVisibility(findViewById<ImageButton>(R.id.ac4_remove), true)
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

        val viewpager = findViewById<ViewPager>(R.id.ac4_imagepreview)
        viewpager.adapter = ImageAdapter(this, Gallery.getPhotos().toTypedArray())

        recyclerView.adapter = GalleryAdapter(Gallery.getPhotos(),this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        //BUG kotlin.UninitializedPropertyAccessException : holder has not been initialized
        if(Gallery.getPhotos()[0].isInitialized())
            Gallery.setEditMode(Gallery.isEditMode(), this)
    }

    private fun select(photo : Photo, position: Int){
        Gallery.setSelected(photo, position, this)
        photo.holder.checkBox.isChecked = !photo.holder.checkBox.isChecked
    }

    private fun removePhoto(){
        val minValue = Gallery.getSelected().last()
        Gallery.getSelectedPhotos().forEach{
            it.holder.photo.alpha = 1f
            it.isExpanded(false)
        }
        Gallery.getSelected().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it)
        }
        Gallery.removePhotos(this)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue,Gallery.getPhotos().size)
        Gallery.setViewVisibility(findViewById(R.id.ac4_remove), false)
        Gallery.setEditMode(false, this)
        viewBinding.ac4Imagepreview.visibility = View.GONE
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
}
