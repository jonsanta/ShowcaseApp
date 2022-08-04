package com.example.showcaseApp.classes

import java.io.File

class Photo(private val file : File, private val thumbnail : File) {
    private var showing : Boolean = false

    fun getThumbnail() : File{
        return thumbnail
    }

    fun getFile() : File{
        return file
    }

    //Sets if the photo has been opened/closed in ViewPager2
    fun setShowing(flag : Boolean){
        showing = flag
    }

    //Returns {@code true} if the photo is being shown in ViewPager2
    fun isShowing() : Boolean{
        return showing
    }

    //Remove files from device
    fun removeFile(){
        file.delete()
        thumbnail.delete()
    }
}