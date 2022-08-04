package com.example.showcaseApp.classes

import android.content.Context
import android.os.Build
import android.view.View
import com.example.showcaseApp.R
import com.example.showcaseApp.databinding.GalleryActivityBinding
import java.io.File
import java.text.MessageFormat
import java.util.*

class Gallery {
    companion object{
        private var editMode = false

        private val photos = mutableListOf<Photo>()
        private val selection = mutableMapOf<Photo, Int>()
        private var selectedPhotoPos : Int = -1

        // Enables UI for editing mode
        fun setEditMode(flag : Boolean, galleryActivityBinding: GalleryActivityBinding){
            val btnDiscard = galleryActivityBinding.ac4BtnDiscard
            setViewVisibility(galleryActivityBinding.ac4SelectText, flag)
            setViewVisibility(galleryActivityBinding.ac4Remove, flag)
            if(flag)
                btnDiscard.setImageResource(R.drawable.cancelar)
            else {
                btnDiscard.setImageResource(R.drawable.arrow)
                selection.clear()
            }

            countSelected(galleryActivityBinding)
            editMode = flag
            galleryActivityBinding.ac4RecyclerView.adapter?.notifyItemRangeChanged(0, photos.size)
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

        fun removePhotos(galleryActivityBinding: GalleryActivityBinding){
            for(photo in selection)
                photo.key.removeFile()
            photos.removeAll(selection.keys)
            clearSelection()
            countSelected(galleryActivityBinding)
        }

        fun clearPhotos(){
            photos.clear()
        }

        fun setSelection(photo : Photo, position : Int, galleryActivityBinding: GalleryActivityBinding){
            selection[photo] = position
            countSelected(galleryActivityBinding)
        }

        fun getSelection() : MutableMap<Photo, Int>{
            return selection
        }

        fun removeSelectionItem(photo: Photo, galleryActivityBinding: GalleryActivityBinding){
            selection.remove(photo)
            countSelected(galleryActivityBinding)
        }

        fun clearSelection(){
            selection.clear()
        }

        fun setSelectedPhotoPos(position : Int){
            selectedPhotoPos = position
        }

        fun getSelectedPhotoPos() : Int{
            return selectedPhotoPos
        }

        // Selection Text on EditMode
        private fun countSelected(galleryActivityBinding: GalleryActivityBinding){
            val textview = galleryActivityBinding.ac4SelectText
            val format = galleryActivityBinding.root.resources.getText(R.string.count_images).toString()
            textview.text = MessageFormat.format(format, selection.size)
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