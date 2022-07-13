package com.example.showcaseApp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(private val map: Map<String, Bitmap>, private val activity : MainActivity4) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = map.keys.toTypedArray().get(position)
        Gallery.setViews(name, holder)

        holder.photo.setImageBitmap(map.get(name))
        holder.photo.adjustViewBounds = true

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = Gallery.getSelectedImages().contains(name)

        // Click Listener
        onClick(holder, name)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return map.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val photo: ImageButton = itemView.findViewById(R.id.photo)
        val checkBox : CheckBox = itemView.findViewById(R.id.checkBox)
    }

    fun onClick(holder : ViewHolder, name: String){
        holder.photo.setOnLongClickListener() {
            if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
                Gallery.setEditMode(true, activity)// Enables editMode
                buttonAction(holder, name)
            }
            else buttonAction(holder, name)
            true
        }

        holder.photo.setOnClickListener(){
            if(Gallery.isEditMode()) buttonAction(holder, name) // CLICK EVENT - while editMode
        }
    }

    fun buttonAction(holder : ViewHolder, name : String){
        Gallery.setSelectedImages(name, activity) // ADD-REMOVE Image from Set
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }


}