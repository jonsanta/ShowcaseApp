package com.example.showcaseApp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ImagePreviewBinding
import com.squareup.picasso.Picasso
import java.io.*

class ImagePreviewFragment(private val file : File, private val activity: CameraActivity) : Fragment(){
    private lateinit var viewBinding : ImagePreviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ImagePreviewBinding.inflate(layoutInflater)

        Picasso.get().load(Uri.parse(file.toUri().toString())).noFade().fit().centerCrop().into(viewBinding.ppfImage)

        viewBinding.ipfBtnSave.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val copy = Utils.copyFile(FileInputStream(file), File("${activity.getExternalFilesDir(null)}/images/"+file.name))
            file.delete()
            Gallery.setSinglePhoto(Photo(copy, makeThumbnailFile(copy)))
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }

        viewBinding.ipfBtnDel.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            file.delete()
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }

        return viewBinding.root.rootView
    }

    private fun makeThumbnailFile(source: File): File {
        val thumbnailSize = 1000
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(source.path, bounds)

        val originalSize = if (bounds.outHeight > bounds.outWidth) bounds.outHeight else bounds.outWidth
        val opts = BitmapFactory.Options()
        opts.inSampleSize = originalSize / thumbnailSize

        val bitmap = BitmapFactory.decodeFile(source.path, opts)
        //create a file to write bitmap data

        val file = File("${activity.getExternalFilesDir(null)}/images/thumbnails/"+source.name+".jpg")
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    }
}