package com.example.showcaseApp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.squareup.picasso.Picasso

class GalleryAdapter(private val list: List<Photo>, private val galleryListener: GalleryListener) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemPosition = position
        val photo = list[position]

        Picasso.get().load(Uri.fromFile(photo.getThumbnail())).noFade().fit().centerCrop().into(holder.photo)

        holder.photo.adjustViewBounds = true

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = Gallery.isSelected(photo)

        holder.photo.setOnLongClickListener{
            galleryListener.onLongItemClick(photo, position)
            true
        }

        holder.photo.setOnClickListener {
            Utils.preventTwoClick(it)
            galleryListener.onShortItemClick(photo, position)
        }

        photo.holder = holder
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
        var itemPosition : Int = 0
    }

    interface GalleryListener{
        fun onLongItemClick(photo : Photo, position: Int)
        fun onShortItemClick(photo : Photo, position: Int)
    }
}