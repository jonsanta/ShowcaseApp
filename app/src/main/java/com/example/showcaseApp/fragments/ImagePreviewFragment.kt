package com.example.showcaseApp.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ImagePreviewBinding
import java.io.*

//Camera captured image preview functionality
class ImagePreviewFragment : Fragment(){
    private lateinit var viewBinding : ImagePreviewBinding
    private lateinit var cameraActivity: CameraActivity

    private lateinit var tempFile: File

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ImagePreviewBinding.inflate(layoutInflater)
        cameraActivity = requireActivity() as CameraActivity
        val args : ImagePreviewFragmentArgs by navArgs()
        tempFile = File(args.file)
        cameraActivity.findViewById<ImageButton>(R.id.ac3_btn_volver).visibility = View.GONE


        return viewBinding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        Glide.with(view).load(tempFile).into(viewBinding.ppfImage)

        viewBinding.ipfBtnSave.setOnClickListener {
            Utils.preventTwoClick(it)
            val copy = Utils.copyFile(FileInputStream(tempFile), File("${cameraActivity.getExternalFilesDir(null)}/images/"+tempFile.name))
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
        tempFile.delete()
    }

    //Generate captures photo Thumbnail
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    }
}