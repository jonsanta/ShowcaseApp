package com.example.showcaseApp.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Utils

/** Contact icon list selection RecyclerView adapter
 * @param images : list of icon Uri's
 * @param onImageClickListener : Icon click Listener
 */
class IconListAdapter(private val images : List<String>, private val onImageClickListener: OnImageClickListener) : RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.photo.adjustViewBounds = true
        Glide.with(holder.itemView).load(images[position]).fitCenter().centerCrop().into(holder.photo)

        holder.photo.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            onImageClickListener.onImageClick(holder.photo.drawable.toBitmap())
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

    interface OnImageClickListener {
        fun onImageClick(data : Bitmap)
    }
}