package com.example.showcaseApp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import com.example.showcaseApp.classes.Photo
import com.squareup.picasso.Picasso

class GalleryAdapter(private val list: List<Photo>, private val activity : GalleryActivity) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photo = list[position]

        Picasso.get().load(Uri.fromFile(photo.getFile())).noFade().fit().centerCrop().into(holder.photo)
        photo.setView(holder)

        holder.photo.adjustViewBounds = true

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = photo.isSelected()

        // Click Listener
        onClick(holder, photo)
        photo.setView(holder)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    private fun onClick(holder : ViewHolder, photo : Photo){
        holder.photo.setOnLongClickListener{
            if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
                Gallery.setEditMode(true, activity)// Enables editMode
                buttonAction(holder, photo)
            }
            else buttonAction(holder, photo)
            true
        }

        holder.photo.setOnClickListener{
            if(Gallery.isEditMode()) buttonAction(holder, photo) // CLICK EVENT - while editMode
        }
    }

    private fun buttonAction(holder : ViewHolder, photo : Photo){
        photo.setSelected(!photo.isSelected())
        Gallery.countSelectedPhotos(activity)
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
    }
}