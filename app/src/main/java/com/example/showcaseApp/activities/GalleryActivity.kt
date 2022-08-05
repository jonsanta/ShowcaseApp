package com.example.showcaseApp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.adapters.ImageAdapter
import com.example.showcaseApp.classes.*
import com.example.showcaseApp.databinding.GalleryActivityBinding

class GalleryActivity : AppCompatActivity(), GalleryAdapter.GalleryAdapterListener{
    private lateinit var viewBinding : GalleryActivityBinding

    private val READ_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = GalleryActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            loadGallery()
        else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_REQUEST_CODE)

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

    /**
     * Overrides onBacPressed method --> Navigation back button pressed
     * Button will perform different actions depending on:
     * 1. if Gallery is in Edit mode : Disable Edit mode
     * 2. if Gallery is showing a photo in ViewPager: Close ViewPager and runs shrink animation for selected item
     * 3. else: Close activity
     */
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

    /**
     * GalleryAdapter.GalleryListener interface method
     * Called on GalleryAdapter item onLongClick
     * Button will perform different actions depending on:
     * 1. Set Gallery.editMode = true & select photo
     * 2. select photo
     * @param holder : clicked items ViewHolder
     * @param photo : clicked items Photo
     * @param position : clicked items RecyclerViews holder position
     */
    override fun onLongItemClick(holder : GalleryAdapter.ViewHolder, photo: Photo, position: Int) {
        if(!Gallery.isEditMode()) {
            Gallery.setEditMode(true, viewBinding)
            select(holder, photo, position)
        }
        else select(holder, photo, position)
    }

    /**
     * GalleryAdapter.GalleryListener interface method
     * Called on GalleryAdapter item onClick
     * Button will perform different actions depending on:
     * 1. If: Gallery.editMode is true : select photo
     * 2. Else: Open ViewPager on given page (@param position)
     * @param holder : clicked items ViewHolder
     * @param photo : clicked items Photo
     * @param position : clicked items RecyclerViews holder position
     * @param view : pressed button - Will prevent double clicks
     */
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

    /**
     * Load RecyclerView & ViewPager (Layout will depend on configuration.orientation)
     * Checks current EditMode for Portrait <-> Landscape redrawing
     * if ViewPager was showing a photo while Portrait <-> Landscape redrawing will remain opened
     */
    private fun loadGallery()
    {
        Gallery.setPhotos(this)

        val recyclerView = viewBinding.ac4RecyclerView
        val viewpager = viewBinding.ac4Imagepreview
        viewpager.adapter = ImageAdapter(Gallery.getPhotos())

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            setRecyclerView(3)
        else
            setRecyclerView(5)


        recyclerView.adapter = GalleryAdapter(Gallery.getPhotos(),this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)

        Gallery.setEditMode(Gallery.isEditMode(), viewBinding)

        val position = Gallery.getSelectedPhotoPos() //Obtains ViewPager page
        if(position != -1){ //If ViewPager returs a page --> Open ViewPager on given page
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
        val minValue = Gallery.getSelection().values.minOrNull()!!

        Gallery.getSelection().keys.forEach {
            it.setShowing(false) //photo is not showing
        }

        //remove Gallery.selection items from RecyclerView and ViewPager
        Gallery.getSelection().forEach{
            viewBinding.ac4RecyclerView.adapter?.notifyItemRemoved(it.value)
            viewBinding.ac4Imagepreview.adapter?.notifyItemRemoved(it.value)
        }

        Gallery.removePhotos(viewBinding)
        viewBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(minValue, Gallery.getPhotos().size)
        Gallery.setViewVisibility(viewBinding.ac4Remove, false)
        Gallery.setEditMode(false, viewBinding)

        //Close ViewPager
        Gallery.setSelectedPhotoPos(-1)
        viewBinding.ac4Imagepreview.visibility = View.GONE
        viewBinding.ac4Imagepreview.currentItem = 0
        viewBinding.ac4RecyclerView.scrollToPosition(minValue)
    }

    private fun setRecyclerView(spanCount : Int){
        viewBinding.ac4RecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        viewBinding.ac4RecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, -10))
    }
}
