package com.example.showcaseApp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
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

        //Changes selected photo when ViewPager2 page is turned
        viewBinding.ac4Imagepreview.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if(Gallery.getSelectedPhotoPos() != -1) { //(x == -1): ViewPager is not openned. (x > -1): ViewPager is openned at x page
                    viewBinding.ac4RecyclerView.scrollToPosition(position)
                    Gallery.getSelection().keys.forEach {
                        it.setShowing(false)
                    }
                    Gallery.clearSelection()
                    Gallery.setSelection(Gallery.getPhotos()[position], position, viewBinding)
                    Gallery.getPhotos()[position].setShowing(true)
                    Gallery.setSelectedPhotoPos(position)
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
        Gallery.setSelectedPhotoPos(-1)

        if(Gallery.isEditMode()){
            Gallery.setEditMode(false, viewBinding)
        }
        else if(Gallery.getSelection().isNotEmpty()){
            Gallery.setViewVisibility(findViewById(R.id.ac4_remove), false)
            GalleryAnimations(viewBinding).animate(
                viewBinding.ac4RecyclerView.findViewHolderForAdapterPosition(viewBinding.ac4Imagepreview.currentItem) as GalleryAdapter.ViewHolder,
                Gallery.getPhotos()[viewBinding.ac4Imagepreview.currentItem],
                viewBinding.ac4Imagepreview.currentItem,
                viewBinding.ac4Imagepreview,
                viewBinding.ac4RecyclerView)
        }
        else{
            Gallery.clearSelection()
            Gallery.clearPhotos()
            finish()
        }
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
            Gallery.setSelectedPhotoPos(position)
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

        val position = Gallery.getSelectedPhotoPos()
        if(position != -1){
            viewBinding.ac4Imagepreview.visibility = View.VISIBLE
            viewBinding.ac4Imagepreview.setCurrentItem(position, false)
            viewBinding.ac4RecyclerView.scrollToPosition(position)
            Gallery.setSelection(Gallery.getPhotos()[position], position, viewBinding)
            Gallery.getPhotos()[position].setShowing(true)
        }
    }

    private fun select(holder : GalleryAdapter.ViewHolder, photo : Photo, position: Int){
        if(Gallery.getSelection().keys.contains(photo)){
            Gallery.removeSelectionItem(photo,viewBinding)
            holder.checkBox.isChecked = false
        }
        else {
            Gallery.setSelection(photo, position, viewBinding)
            holder.checkBox.isChecked = true
        }
    }

    private fun removePhoto(){
        Gallery.setSelectedPhotoPos(-1)
        val minValue = Gallery.getSelection().values.minOrNull()!!

        Gallery.getSelection().keys.forEach {
            it.setShowing(false)
        }

        Gallery.getSelection().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it.value)
            viewBinding.ac4Imagepreview.adapter?.notifyItemRemoved(it.value)
        }

        Gallery.removePhotos(viewBinding)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue, Gallery.getPhotos().size)
        Gallery.setViewVisibility(viewBinding.ac4Remove, false)
        Gallery.setEditMode(false, viewBinding)
        viewBinding.ac4Imagepreview.visibility = View.GONE
        viewBinding.ac4Imagepreview.currentItem = 0
        viewBinding.ac4RecyclerView.scrollToPosition(minValue)
    }

    private fun setRecyclerView(spanCount : Int){
        viewBinding.ac4RecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        viewBinding.ac4RecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, -10))
    }
}
