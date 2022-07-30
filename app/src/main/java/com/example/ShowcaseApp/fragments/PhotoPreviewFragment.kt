package com.example.showcaseApp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.activities.GalleryActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.PhotoPreviewBinding
import com.squareup.picasso.Picasso
import java.io.File

class PhotoPreviewFragment(private val file : File, private val activity: GalleryActivity) : Fragment(){
    private lateinit var viewBinding: PhotoPreviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = PhotoPreviewBinding.inflate(layoutInflater)
        activity.setPhotoPreviewFragment(this)

        Picasso.get().load(Uri.parse(file.toUri().toString())).noFade().fit().centerCrop().into(viewBinding.ppfImage)

        Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), true)

        activity.findViewById<ImageButton>(R.id.ac4_remove).setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), false)
            activity.removePhoto()
            activity.supportFragmentManager.popBackStack()
            activity.fragmentClosed()
        }

        return viewBinding.root.rootView
    }
}