package com.example.showcaseApp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class ImagesAdapter(private val mList: List<Bitmap>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
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
        MainActivity4.setViews(holder)

        // sets the image to the imageview from our itemHolder class
        holder.photo.setImageBitmap(bitmap)
        holder.photo.adjustViewBounds = true
        //Enables checkbox depending on isEditMode flag && Set true the checkbox if was checked before (Used for portrait -> land redraw)
        holder.checkBox.isClickable = MainActivity4.isEditMode()
        holder.checkBox.isVisible = MainActivity4.isEditMode()
        holder.checkBox.isChecked = MainActivity4.isSelectedPhoto(position)

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
            if(!MainActivity4.isEditMode()) { //If edit mode is false and long click event
                MainActivity4.setSelectedPhotos(position) //Select photo
                MainActivity4.setEditMode(true)// enable editMode
                holder.checkBox.isChecked = true //checkbox true
            }
            else buttonAction(holder, position)
            true
        }

        holder.photo.setOnClickListener(){
            if(MainActivity4.isEditMode()) buttonAction(holder, position) //While editMode is true if click event
        }
    }

    fun buttonAction(holder : ViewHolder, position: Int){
        MainActivity4.setSelectedPhotos(position) //Select-Remove Photo
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }


}