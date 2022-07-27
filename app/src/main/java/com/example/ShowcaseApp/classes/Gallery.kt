package com.example.showcaseApp.classes

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import java.text.MessageFormat

class Gallery {
    companion object{
        private var editMode = false

        val photos = mutableListOf<Photo>()

        // Enables UI for editing mode
        fun setEditMode(flag : Boolean, activity : GalleryActivity){
            for(photo in photos){
                photo.getView()?.checkBox?.isVisible = flag
                if(!flag)
                    photo.getView()?.checkBox?.isChecked = false
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

        fun removePhotos(){
            val temp = mutableSetOf<Photo>()
            for(photo in photos)
            {
                if(photo.isSelected()) {
                    temp.add(photo)
                    photo.removeFile()
                }
            }
            photos.removeAll(temp)
        }

        fun removeViews(){
            for(photo in photos)
                photo.setView(null)
        }

        fun clearSelected(){
            photos.forEach{
                it.setSelected(false)
            }
        }

        private fun countSelected() : Int{
            var index = 0
            photos.forEach{
                if(it.isSelected())
                    index++
            }
            return index
        }

        // Selection Text on EditMode
        fun countSelectedPhotos(activity: GalleryActivity){
            val textview = activity.findViewById<TextView>(R.id.ac4_selectText)
            val format = activity.resources.getText(R.string.count_images).toString()

            textview.text = MessageFormat.format(format, countSelected())
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