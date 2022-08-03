package com.example.showcaseApp.classes

import java.io.File

class Photo(private val file : File, private val thumbnail : File) {
    private var expanded : Boolean = false

    fun getThumbnail() : File{
        return thumbnail
    }

    fun getFile() : File{
        return file
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