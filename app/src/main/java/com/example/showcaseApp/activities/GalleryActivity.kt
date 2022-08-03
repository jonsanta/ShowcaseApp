package com.example.showcaseApp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.adapters.ImageAdapter
import com.example.showcaseApp.classes.*
import com.example.showcaseApp.databinding.GalleryActivityBinding


class GalleryActivity : AppCompatActivity(), GalleryAdapter.GalleryListener{
    private lateinit var viewBinding : GalleryActivityBinding

    private val READ_REQUEST_CODE = 123
    private var selected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = GalleryActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            loadGallery()
        else
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

        viewBinding.ac4BtnDiscard.setOnClickListener {
            Utils.preventTwoClick(it)
            onBackPressed()
        }

        viewBinding.ac4Remove.setOnClickListener {
            Utils.preventTwoClick(it)
            removePhoto()
        }

        viewBinding.ac4Imagepreview.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if(selected) {
                    viewBinding.ac4RecyclerView.scrollToPosition(position)
                    Gallery.getSelectedPhotos().forEach {
                        it.isExpanded(false)
                    }
                    Gallery.clearSelected()
                    Gallery.setSelected(Gallery.getPhotos()[position], position, viewBinding)
                    Gallery.getPhotos()[position].isExpanded(true)
                }
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
            Gallery.setEditMode(false, viewBinding)
        }
        else if(Gallery.getSelected().isNotEmpty()){
            Gallery.setViewVisibility(findViewById(R.id.ac4_remove), false)
            GalleryAnimations(viewBinding).animate(findViewById<RecyclerView>(R.id.ac4_recyclerView).findViewHolderForAdapterPosition(viewBinding.ac4Imagepreview.currentItem) as GalleryAdapter.ViewHolder, Gallery.getPhotos()[viewBinding.ac4Imagepreview.currentItem], viewBinding.ac4Imagepreview.currentItem, viewBinding.ac4Imagepreview, viewBinding.ac4RecyclerView)
        }
        else{
            Gallery.clearSelected()
            Gallery.clearPhotos()
            finish()
        }
        selected = false
    }

    override fun onLongItemClick(holder : GalleryAdapter.ViewHolder, photo: Photo, position: Int) {
        if(!Gallery.isEditMode()) {
            Gallery.setEditMode(true, viewBinding)
            select(holder, photo, position)
        }
        else select(holder, photo, position)
    }

    override fun onShortItemClick(holder : GalleryAdapter.ViewHolder, photo: Photo, position: Int, view : View) {
        if(Gallery.isEditMode())
            select(holder, photo, position)
        else {
            Utils.preventTwoClick(view)
            GalleryAnimations(viewBinding).animate(holder, photo, position, viewBinding.ac4Imagepreview, viewBinding.ac4RecyclerView)
            Gallery.setViewVisibility(viewBinding.ac4Remove, true)
            selected = true
        }
    }

    private fun loadGallery()
    {
        Gallery.setPhotos(this)

        val recyclerView = viewBinding.ac4RecyclerView
        val viewpager = viewBinding.ac4Imagepreview
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRecyclerView(3)
            viewpager.adapter = ImageAdapter(Gallery.getPhotos(), false)
        }
        else{
            setRecyclerView(5)
            viewpager.adapter = ImageAdapter(Gallery.getPhotos(), true)
        }

        recyclerView.adapter = GalleryAdapter(Gallery.getPhotos(),this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        Gallery.setEditMode(Gallery.isEditMode(), viewBinding)
    }

    private fun select(holder : GalleryAdapter.ViewHolder, photo : Photo, position: Int){
        if(Gallery.getSelectedPhotos().contains(photo)){
            Gallery.removeSelected(photo,viewBinding)
            holder.checkBox.isChecked = false
        }
        else {
            Gallery.setSelected(photo, position, viewBinding)
            holder.checkBox.isChecked = true
        }
    }

    private fun removePhoto(){
        val minValue = Gallery.getSelected().last()

        Gallery.getSelected().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it)
            viewBinding.ac4Imagepreview.adapter?.notifyItemRemoved(it)
            Gallery.getPhotos()[it].isExpanded(false)
        }
        Gallery.removePhotos(viewBinding)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue, Gallery.getPhotos().size)
        Gallery.setViewVisibility(viewBinding.ac4Remove, false)
        Gallery.setEditMode(false, viewBinding)
        viewBinding.ac4Imagepreview.visibility = View.GONE
        viewBinding.ac4Imagepreview.currentItem = 0
        viewBinding.ac4RecyclerView.scrollToPosition(minValue)
        selected = false
    }

    private fun setRecyclerView(spanCount : Int){
        viewBinding.ac4RecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        viewBinding.ac4RecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, -10))
    }
}
