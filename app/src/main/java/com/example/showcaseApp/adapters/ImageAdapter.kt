package com.example.showcaseApp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Photo
import com.github.chrisbanes.photoview.PhotoView

class ImageAdapter(private val list : List<Photo>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = list[position]

        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(Drawable.createFromPath(photo.getThumbnail().path))

        Glide.with(holder.imageView.context)
            .load(photo.getFile().toUri())
            .apply(requestOptions)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: PhotoView = itemView.findViewById(R.id.imageView)
    }
}