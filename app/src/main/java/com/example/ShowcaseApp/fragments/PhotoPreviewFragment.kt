package com.example.showcaseApp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import com.example.showcaseApp.classes.Gallery
import com.squareup.picasso.Picasso
import java.io.File

class PhotoPreviewFragment(private val file : File, private val activity: GalleryActivity) : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.photo_preview, container, false)
        activity.setPhotoPreviewFragment(this)

        Picasso.get().load(Uri.parse(file.toUri().toString())).noFade().fit().centerCrop().into(view.findViewById<ImageView>(R.id.ppf_image))

        Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), true)

        activity.findViewById<ImageButton>(R.id.ac4_remove).setOnClickListener{
            Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), false)
            activity.removePhoto()
            activity.supportFragmentManager.popBackStack()
            activity.fragmentClosed()
        }

        return view
    }
}