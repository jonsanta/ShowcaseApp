package com.example.showcaseApp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.squareup.picasso.Picasso
import java.io.*

class ImagePreviewFragment(private val file : File, private val activity: CameraActivity) : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.image_preview, container, false)

        Picasso.get().load(Uri.parse(file.toUri().toString())).noFade().fit().centerCrop().into(view.findViewById<ImageView>(R.id.ppf_image))

        view.findViewById<ImageButton>(R.id.ipf_btn_save).setOnClickListener{
            val copy = copyFile(file)
            file.delete()
            Gallery.setSinglePhoto(Photo(copy, makeThumbnailFile(copy, 1000)))
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }
        view.findViewById<ImageButton>(R.id.ipf_btn_del).setOnClickListener{
            file.delete()
            activity.supportFragmentManager.popBackStack()
            CameraActivity.isAvailable(true)
        }
        return view
    }

    private fun copyFile(src: File?) : File{
        val copy = File("${activity.getExternalFilesDir(null)}/images/"+file.name)
        FileInputStream(src).use { `in` ->
            FileOutputStream(copy).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
        return copy
    }

    private fun makeThumbnailFile(source: File, thumbnailSize : Int): File { // File name like "image.png"
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