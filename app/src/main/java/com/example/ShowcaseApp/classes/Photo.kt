package com.example.showcaseApp.classes

import com.example.showcaseApp.adapters.GalleryAdapter
import java.io.File

class Photo(private val file : File) {

    private var view : GalleryAdapter.ViewHolder? = null

    fun setView(view : GalleryAdapter.ViewHolder?){
        this.view = view
    }

    fun getView() : GalleryAdapter.ViewHolder?{
        return view
    }

    fun getFile() : File{
        return file
    }
}