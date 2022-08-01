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
import com.example.showcaseApp.activities.GalleryActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.GalleryAnimations
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.squareup.picasso.Picasso

class GalleryAdapter(private val list: List<Photo>, private val galleryActivity: GalleryActivity) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(), GalleryActivity.GalleryActivityListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photo = list[position]

        Picasso.get().load(Uri.fromFile(photo.getThumbnail())).noFade().fit().centerCrop().into(holder.photo)

        holder.photo.adjustViewBounds = true

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = Gallery.isSelected(photo)

        photo.setView(holder)
        photo.setPosition(position)
        onClick(photo)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onClickListener(photo: Photo, duration: Int) {
        galleryActivity.galleryActivityListener = this
        GalleryAnimations(galleryActivity).animate(photo, duration)
    }

    private fun onClick(photo : Photo){
        val holder = photo.getView()!!
        val position = photo.getPosition()

        holder.photo.setOnLongClickListener{
            if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
                Gallery.setEditMode(true, galleryActivity)// Enables editMode
                select(holder, photo, position)
            }
            else select(holder, photo, position)
            true
        }

        holder.photo.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if(Gallery.isEditMode()) select(holder, photo, position)
            else{
                onClickListener(photo, 150)
            }
        }
    }

    private fun select(holder : ViewHolder, photo : Photo, position: Int){
        Gallery.setSelected(photo, position, galleryActivity)
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
        var expanded : Boolean = false
    }
}