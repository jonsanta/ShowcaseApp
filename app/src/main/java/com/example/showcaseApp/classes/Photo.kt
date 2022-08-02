package com.example.showcaseApp.classes

import com.example.showcaseApp.adapters.GalleryAdapter
import java.io.File

class Photo(private val file : File, private val thumbnail : File) {
    lateinit var holder : GalleryAdapter.ViewHolder
    private var expanded : Boolean = false

    fun getThumbnail() : File{
        return thumbnail
    }

    fun getFile() : File{
        return file
    }

    fun isInitialized() : Boolean{
        return this::holder.isInitialized
    }

    fun isExpanded(flag : Boolean = expanded) : Boolean{
        expanded = flag
        return expanded
    }

    fun removeFile(){
        file.delete()
        thumbnail.delete()
    }
}