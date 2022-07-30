package com.example.showcaseApp.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.squareup.picasso.Picasso

class IconListAdapter(private val images : List<String>, private val alertDialog: AlertDialog, private val onImageClickListener: OnImageClickListener) : RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(images[position]).noFade().fit().centerCrop().into(holder.photo)
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
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)

    }
}