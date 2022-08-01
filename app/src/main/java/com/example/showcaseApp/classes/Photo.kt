package com.example.showcaseApp.classes

import com.example.showcaseApp.adapters.GalleryAdapter
import java.io.File

class Photo(private val file : File, private val thumbnail : File) {
    lateinit var holder : GalleryAdapter.ViewHolder
    private var position : Int? = null

    fun getThumbnail() : File{
        return thumbnail
    }

    fun getFile() : File{
        return file
    }

    fun setPosition(position : Int){
        this.position = position
    }

    fun getPosition() : Int{
        return position!!
    }

    fun isInitialized() : Boolean{
        return this::holder.isInitialized
    }

    fun removeFile(){
        file.delete()
        thumbnail.delete()
    }
}