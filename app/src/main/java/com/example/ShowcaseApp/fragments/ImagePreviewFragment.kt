package com.example.showcaseApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.Gallery
import java.io.File

class ImagePreviewFragment(private val file : File, private val activity: CameraActivity) : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.image_preview, container, false)

        //view.findViewById<ImageView>(R.id.ipf_image).setImageBitmap(Gallery.setBitmap(file))

        view.findViewById<ImageButton>(R.id.ipf_btn_save).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }
        view.findViewById<ImageButton>(R.id.ipf_btn_del).setOnClickListener{
            Gallery.removeBitmap(file.name)
            file.delete()
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }
        return view
    }
}