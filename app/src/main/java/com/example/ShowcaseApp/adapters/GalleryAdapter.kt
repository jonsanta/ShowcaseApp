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
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.fragments.PhotoPreviewFragment
import com.squareup.picasso.Picasso

class GalleryAdapter(private val list: List<Photo>, private val activity : GalleryActivity) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
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

        // Click Listener
        onClick(holder, photo, position)
        photo.setView(holder)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    private fun onClick(holder : ViewHolder, photo : Photo, position : Int){

        holder.photo.setOnLongClickListener{
            if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
                Gallery.setEditMode(true, activity)// Enables editMode
                select(holder, photo, position)
            }
            else select(holder, photo, position)
            true
        }

        holder.photo.setOnClickListener{
            if(Gallery.isEditMode()) select(holder, photo, position) // CLICK EVENT - while editMode
            else{
                Gallery.setSelected(photo, position, activity)
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                transaction.add(R.id.ac4_fragment, PhotoPreviewFragment(photo.getFile(), activity)).commit()
            }
        }
    }

    private fun select(holder : ViewHolder, photo : Photo, position: Int){
        Gallery.setSelected(photo, position, activity)
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
    }
}