package com.example.showcaseApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Photo
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher

class ImageAdapter(private val list : List<Photo>, private val land : Boolean) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View
        if(land)
            view = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer_land, parent, false)
        else
            view = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = list[position]

        val pAttacher = PhotoViewAttacher(holder.imageView)
        pAttacher.update()

        //holder.imageView.setImageURI(photo.getFile().toUri())
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(holder.imageView).load(photo.getFile()).apply(requestOptions).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: PhotoView = itemView.findViewById(R.id.imageView)
    }
}