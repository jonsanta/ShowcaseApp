package com.example.showcaseApp.classes

import com.example.showcaseApp.adapters.GalleryAdapter
import java.io.File

class Photo(private val file : File, private val thumbnail : File) {

    private var view : GalleryAdapter.ViewHolder? = null

    fun setView(view : GalleryAdapter.ViewHolder?){
        this.view = view
    }

    fun getView() : GalleryAdapter.ViewHolder?{
        return view
    }

    fun getThumbnail() : File{
        return thumbnail
    }

    fun getFile() : File{
        return file
    }

    fun removeFile(){
        file.delete()
        thumbnail.delete()
    }
}