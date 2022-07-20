package com.example.showcaseApp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class IconListAdapter(private val images : Map<String, Bitmap>, private val alertDialog: AlertDialog) : RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = images.keys.toTypedArray()[position]

        holder.photo.setImageBitmap(images[name])
        holder.photo.adjustViewBounds = true

        holder.photo.setOnClickListener{
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