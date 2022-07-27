package com.example.showcaseApp.classes

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import java.text.MessageFormat

class Gallery {
    companion object{
        private var editMode = false

        private lateinit var recyclerView : RecyclerView

        val photos = mutableMapOf<String, Photo>()

        private val selectedImages = mutableSetOf<String>() // Contains: map Keys = Image names

        // Enables UI for editing mode
        fun setEditMode(flag : Boolean, activity : GalleryActivity){
            for(view in photos.values.toTypedArray()){
                view.getView()?.checkBox?.isVisible = flag
                if(!flag)
                    view.getView()?.checkBox?.isChecked = false
            }
            val btnDiscard = activity.findViewById<ImageButton>(R.id.ac4_btn_discard)
            setViewVisibility(activity.findViewById<TextView>(R.id.ac4_selectText), flag)
            setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), flag)
            if(flag)
                btnDiscard.background =  AppCompatResources.getDrawable(activity.applicationContext, R.drawable.cancelar)
            else
                btnDiscard.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.arrow)

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
            Companion.recyclerView = recyclerView
        }

        fun removeBitmap(item : String){
            photos.remove(item)
        }

        fun clearViews(){
            for(photo in photos)
                photo.value.setView(null)
        }

        // Add-Remove from selection list
        fun setSelectedImages(photo: String, activity: GalleryActivity){
            if(selectedImages.contains(photo)) selectedImages.remove(photo)
            else selectedImages.add(photo)

            countSelectedPhotos(activity)
        }

        fun getSelectedImages() : MutableSet<String>{
            return selectedImages
        }

        // Selection Text on EditMode
        fun countSelectedPhotos(activity: GalleryActivity){
            val textview = activity.findViewById<TextView>(R.id.ac4_selectText)
            val format = activity.resources.getText(R.string.count_images).toString()

            textview.text = MessageFormat.format(format, selectedImages.size)
        }

        // Enable-Disable EditMode views (Top box & buttons)
        private fun setViewVisibility(view : View, flag: Boolean) {
            view.isEnabled = flag
            if(flag)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.INVISIBLE
        }
    }
}