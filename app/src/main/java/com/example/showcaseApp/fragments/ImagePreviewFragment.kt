package com.example.showcaseApp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.navArgs
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.FragmentCameraBinding
import com.example.showcaseApp.databinding.ImagePreviewBinding
import com.squareup.picasso.Picasso
import java.io.*

class ImagePreviewFragment : Fragment(){
    private lateinit var viewBinding : ImagePreviewBinding
    private lateinit var cameraActivity: CameraActivity

    private lateinit var file: File

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ImagePreviewBinding.inflate(layoutInflater)
        cameraActivity = requireActivity() as CameraActivity
        val args : ImagePreviewFragmentArgs by navArgs()
        file = File(args.file)
        cameraActivity.findViewById<ImageButton>(R.id.ac3_btn_volver).visibility = View.GONE


        return viewBinding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        Picasso.get().load(file).noFade().fit().centerCrop().into(viewBinding.ppfImage)

        viewBinding.ipfBtnSave.setOnClickListener {
            Utils.preventTwoClick(it)
            val copy = Utils.copyFile(FileInputStream(file), File("${cameraActivity.getExternalFilesDir(null)}/images/"+file.name))
            Gallery.setSinglePhoto(Photo(copy, makeThumbnailFile(copy)))
            closeFragment()
        }

        viewBinding.ipfBtnDel.setOnClickListener {
            Utils.preventTwoClick(it)
            closeFragment()
        }
    }

    private fun closeFragment(){
        navController.popBackStack()
        cameraActivity.findViewById<ImageButton>(R.id.ac3_btn_volver).visibility = View.VISIBLE
        CameraFragment.isAvailable(true)
        file.delete()
    }

    private fun makeThumbnailFile(source: File): File {
        val thumbnailSize = 700
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(source.path, bounds)

        val originalSize = if (bounds.outHeight > bounds.outWidth) bounds.outHeight else bounds.outWidth
        val opts = BitmapFactory.Options()
        opts.inSampleSize = originalSize / thumbnailSize

        val bitmap = BitmapFactory.decodeFile(source.path, opts)
        //create a file to write bitmap data

        val file = File("${cameraActivity.getExternalFilesDir(null)}/images/thumbnails/"+source.name+".jpg")
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