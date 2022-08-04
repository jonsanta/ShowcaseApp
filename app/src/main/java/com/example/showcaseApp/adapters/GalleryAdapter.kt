package com.example.showcaseApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo

class GalleryAdapter(private val list: List<Photo>, private val galleryListener: GalleryListener) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = list[position]

        holder.photo.adjustViewBounds = true
        Glide.with(holder.itemView).load(photo.getThumbnail()).centerCrop().into(holder.photo)

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = Gallery.getSelection().contains(photo)

        holder.photo.setOnLongClickListener{
            galleryListener.onLongItemClick(holder, photo, position)
            true
        }

        holder.photo.setOnClickListener {
            galleryListener.onShortItemClick(holder, photo, position, it)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
    }

    interface GalleryListener{
        fun onLongItemClick(holder : ViewHolder, photo : Photo, position: Int)
        fun onShortItemClick(holder : ViewHolder, photo : Photo, position: Int, view : View)
    }
}