package com.example.showcaseApp.classes

import com.example.showcaseApp.adapters.GalleryAdapter
import java.io.File

class Photo(private val file : File) {

    private var view : GalleryAdapter.ViewHolder? = null
    private var selected : Boolean = false

    fun setView(view : GalleryAdapter.ViewHolder?){
        this.view = view
    }

    fun getView() : GalleryAdapter.ViewHolder?{
        return view
    }

    fun getFile() : File{
        return file
    }

    fun removeFile(){
        file.delete()
    }

    fun isSelected() : Boolean{
        return selected
    }

    fun setSelected(flag : Boolean){
        selected = flag
    }
}