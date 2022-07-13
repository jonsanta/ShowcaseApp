package com.example.showcaseApp

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.text.MessageFormat

class Gallery {
    companion object{
        private var editMode = false

        private lateinit var recyclerView : RecyclerView

        private val bitmaps = mutableMapOf<String, Bitmap>() // Key: image name - value: portrait bitmap
        private val landBitmaps = mutableMapOf<String, Bitmap>()// Key: image name - value: landscape bitmap
        private val views = mutableMapOf<String, GalleryAdapter.ViewHolder>() // Contains: RecyclerView items
        private val selectedImages = mutableSetOf<String>() // Contains: map Keys = Image names

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

        fun isRecyclerViewInitialized() : Boolean{
            return this::recyclerView.isInitialized
        }

        fun setRecyclerView(recyclerView: RecyclerView){
            this.recyclerView = recyclerView
        }

        fun getRecyclerView() : RecyclerView{
            return recyclerView
        }

        //Generates a single Bitmap
        fun setBitmap(file : File){
            val fileInputStream = FileInputStream(file)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val bitmap = BitmapFactory.decodeStream(fileInputStream, null, options)
            if (bitmap != null) {
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, Resources.getSystem().displayMetrics.widthPixels, 1500, true)
                val landBitmap = Bitmap.createScaledBitmap(bitmap, 730, 800, true)
                bitmaps[file.name] = resizedBitmap // Populate portrait bitmaps map --> For RecyclerView
                landBitmaps[file.name] = landBitmap // Populate landscape bitmaps map --> For RecyclerView
            }
        }

        fun removeBitmap(item : String){
            bitmaps.remove(item)
            landBitmaps.remove(item)
            views.remove(item)
        }

        fun getBitmaps(land: Boolean) : Map<String, Bitmap>{
            if(!land) return bitmaps
            else return landBitmaps
        }

        fun setViews(name: String, holder: GalleryAdapter.ViewHolder){
            views[name] = holder
        }

        fun clearViews(){
            views.clear()
        }

        // Add-Remove from selection list
        fun setSelectedImages(photo: String, activity: MainActivity4){
            if(selectedImages.contains(photo)) selectedImages.remove(photo)
            else selectedImages.add(photo)

            countSelectedPhotos(activity)
        }

        fun getSelectedImages() : MutableSet<String>{
            return selectedImages
        }

        // Selection Text on EditMode
        fun countSelectedPhotos(activity: MainActivity4){
            val textview = activity.findViewById<TextView>(R.id.selectText)
            val format = activity.resources.getText(R.string.count_images).toString()

            textview.text = MessageFormat.format(format, selectedImages.size)
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