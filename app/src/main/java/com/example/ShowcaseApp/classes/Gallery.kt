package com.example.showcaseApp.classes

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import java.io.File
import java.text.MessageFormat

class Gallery {
    companion object{
        private var editMode = false

        private val photos = mutableListOf<Photo>()
        private val selectedPhotos = mutableMapOf<Photo, Int>()

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

            countSelected(activity)
            editMode = flag
        }

        fun isEditMode() : Boolean{
            return editMode
        }

        fun setSinglePhoto(photo : Photo){
            photos.add(photo)
        }

        fun setPhotos(context: Context){
            val directory = removeThumbnailDirectory(File("${context.getExternalFilesDir(null)}/images/").listFiles())
            val thumbnails = File("${context.getExternalFilesDir(null)}/images/thumbnails/").listFiles()

            //Loads gallery images
            if (photos.size < directory.size && directory.isNotEmpty() && thumbnails != null) {
                photos.clear()
                directory.forEachIndexed{index, file ->
                    photos.add(Photo(file, thumbnails[index]))
                }
            }
        }

        fun getPhotos() : List<Photo>{
            return photos
        }

        fun removePhotos(activity: GalleryActivity){
            for(photo in selectedPhotos)
                photo.key.removeFile()
            photos.removeAll(selectedPhotos.keys)
            clearSelected()
            countSelected(activity)
        }

        fun setSelected(photo : Photo, position : Int, activity: GalleryActivity){
            if(selectedPhotos.contains(photo))
                selectedPhotos.remove(photo)
            else
                selectedPhotos[photo] = position

            countSelected(activity)
        }

        fun getSelected() : List<Int>{
            return selectedPhotos.values.toList().sortedDescending()
        }

        fun isSelected(photo: Photo) : Boolean{
            return selectedPhotos.contains(photo)
        }

        fun clearSelected(){
            selectedPhotos.clear()
        }

        // Selection Text on EditMode
        private fun countSelected(activity: GalleryActivity){
            val textview = activity.findViewById<TextView>(R.id.ac4_selectText)
            val format = activity.resources.getText(R.string.count_images).toString()
            textview.text = MessageFormat.format(format, selectedPhotos.size)
        }

        // Enable-Disable EditMode views (Top box & buttons)
        private fun setViewVisibility(view : View, flag: Boolean) {
            view.isEnabled = flag
            if(flag)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.INVISIBLE
        }

        private fun removeThumbnailDirectory(arr: Array<File>?) : Array<File>{
            if(arr != null) {
                val arrList: MutableList<File> = arr.toMutableList()
                arrList.removeAt(0)
                return arrList.toTypedArray()
            }
            return emptyArray()
        }
    }
}