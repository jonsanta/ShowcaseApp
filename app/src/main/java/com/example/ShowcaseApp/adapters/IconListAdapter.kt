package com.example.showcaseApp.adapters

import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R

class IconListAdapter(private val images : List<Bitmap>, private val alertDialog: AlertDialog, private val onImageClickListener: OnImageClickListener) : RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.photo.setImageBitmap(images[position])
        holder.photo.adjustViewBounds = true

        holder.photo.setOnClickListener{
            onImageClickListener.onImageClick(holder.photo.drawable.toBitmap())

            alertDialog.cancel()
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return images.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)

    }
}