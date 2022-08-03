package com.example.showcaseApp.classes

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import java.io.File
import java.text.MessageFormat
import java.util.*

class Gallery {
    companion object{
        private var editMode = false

        private val photos = mutableListOf<Photo>()
        private val selectedPhotos = mutableMapOf<Photo, Int>()

        // Enables UI for editing mode
        fun setEditMode(flag : Boolean, activity : GalleryActivity){
            val btnDiscard = activity.findViewById<ImageButton>(R.id.ac4_btn_discard)
            setViewVisibility(activity.findViewById<TextView>(R.id.ac4_selectText), flag)
            setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), flag)
            if(flag) {
                btnDiscard.setImageResource(R.drawable.cancelar)
                selectedPhotos.clear()
            }
            else
                btnDiscard.setImageResource(R.drawable.arrow)

            countSelected(activity)
            editMode = flag
            activity.findViewById<RecyclerView>(R.id.ac4_recyclerView).adapter?.notifyItemRangeChanged(0, photos.size)
        }

        fun isEditMode() : Boolean{
            return editMode
        }

        // Enable-Disable EditMode views (Top box & buttons)
        fun setViewVisibility(view : View, flag: Boolean) {
            view.isEnabled = flag
            if(flag)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.INVISIBLE
        }

        fun setSinglePhoto(photo : Photo){
            photos.add(0, photo)
        }

        fun setPhotos(context: Context){
            val directory = removeThumbnailDirectory(File("${context.getExternalFilesDir(null)}/images/").listFiles())
            val thumbnails = File("${context.getExternalFilesDir(null)}/images/thumbnails/").listFiles()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(directory, Comparator.comparingLong(File::lastModified).reversed())
                if (thumbnails != null) {
                    Arrays.sort(thumbnails, Comparator.comparingLong(File::lastModified).reversed())
                }
            }

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
            selectedPhotos[photo] = position
            countSelected(activity)
        }

        fun removeSelected(photo: Photo, activity: GalleryActivity){
            selectedPhotos.remove(photo)
            countSelected(activity)
        }

        fun getSelected() : List<Int>{
            return selectedPhotos.values.toList().sortedDescending()
        }

        fun getSelectedPhotos() : List<Photo>{
            return selectedPhotos.keys.toList()
        }

        fun isSelected(photo: Photo) : Boolean{
            return selectedPhotos.contains(photo)
        }

        fun clearSelected(){
            selectedPhotos.clear()
        }

        fun clearPhotos(){
            photos.clear()
        }

        // Selection Text on EditMode
        private fun countSelected(activity: GalleryActivity){
            val textview = activity.findViewById<TextView>(R.id.ac4_selectText)
            val format = activity.resources.getText(R.string.count_images).toString()
            textview.text = MessageFormat.format(format, selectedPhotos.size)
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