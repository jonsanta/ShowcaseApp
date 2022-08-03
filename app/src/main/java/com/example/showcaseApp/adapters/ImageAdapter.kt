package com.example.showcaseApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Photo

class ImageAdapter(private val list : List<Photo>, private val land : Boolean) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view : View
        if(land)
            view = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer_land, parent, false)
        else
            view = LayoutInflater.from(parent.context).inflate(R.layout.image_viewer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = list[position]

        //holder.imageView.setImageURI(photo.getFile().toUri())
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(holder.imageView).load(photo.getFile()).apply(requestOptions).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}