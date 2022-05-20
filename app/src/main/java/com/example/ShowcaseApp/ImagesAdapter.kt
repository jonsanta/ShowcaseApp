package com.example.ShowcaseApp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ImagesAdapter(private val mList: List<Bitmap>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    var isEditMode = false
    val selectedPhotos = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = mList[position]
        MainActivity4.views.add(holder)

        // sets the image to the imageview from our itemHolder class
        holder.photo.setImageBitmap(bitmap)
        holder.photo.adjustViewBounds = true
        holder.checkBox.isClickable = false

        if(isEditMode) holder.checkBox.isVisible = true
        else holder.checkBox.isVisible = false

        editMode(holder, position)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val photo: ImageButton = itemView.findViewById(R.id.photo)
        val checkBox : CheckBox = itemView.findViewById(R.id.checkBox)
    }
    fun editMode(holder : ViewHolder, position: Int){
        holder.photo.setOnLongClickListener() {
            if(!isEditMode) {
                selectedPhotos.add(position)
                holder.checkBox.isChecked = true
                isEditMode = true
                MainActivity4.editMode()
            }
            else buttonAction(holder, position)
            true
        }

        holder.photo.setOnClickListener(){
            if(isEditMode) buttonAction(holder, position)
        }
    }

    fun buttonAction(holder : ViewHolder, position: Int){
        if(selectedPhotos.contains(position)) selectedPhotos.remove(position)
        else selectedPhotos.add(position)
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }
}