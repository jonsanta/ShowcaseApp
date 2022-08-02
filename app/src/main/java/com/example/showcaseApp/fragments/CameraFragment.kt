package com.example.showcaseApp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.CameraActivity
import com.example.showcaseApp.classes.LuminosityAnalyzer
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var viewBinding: FragmentCameraBinding
    private lateinit var cameraActivity: CameraActivity
    private lateinit var imageCapture: ImageCapture

    private lateinit var navController: NavController

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = FragmentCameraBinding.inflate(layoutInflater)
        cameraActivity = requireActivity() as CameraActivity
        return viewBinding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        cameraActivity.window.navigationBarColor = resources.getColor(R.color.black, resources.newTheme())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            cameraActivity.window.setDecorFitsSystemWindows(false)
        else
            cameraActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN //DEPRECATED BUT STILL WORKING IN OLD VERSIONS

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(cameraActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if (available)
                takePhoto()
        }

        cameraActivity.findViewById<ImageButton>(R.id.ac3_btn_volver).setOnClickListener {
            Utils.preventTwoClick(it)
            cameraActivity.finish()
        }

        viewBinding.ac3BtnSwitch.setOnClickListener {
            Utils.preventTwoClick(it)
            if (available) {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    CameraSelector.DEFAULT_BACK_CAMERA

                startCamera()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        available = false
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture

        val file = File("${cameraActivity.getExternalFilesDir(null)}/temp/"+ SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())+".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, " ${R.string.ac3_error} ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "${R.string.ac3_btn_capture} ${file.path}"
                    Log.d(TAG, msg)
                    val action = CameraFragmentDirections.actionCameraFragmentToImagePreviewFragment(file.path)
                    navController.navigate(action)
                }
            }
        )
    }

    private fun startCamera() {
        available = true
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            @androidx.camera.core.ExperimentalZeroShutterLag
            imageCapture = ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_AUTO).setCaptureMode(
                ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG
            ).setTargetResolution(
                Size(2160,4096)
            ).build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                cameraActivity.finish()
            }
        }
    }

    companion object {
        private var available = true
        private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        private const val TAG = "CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun isAvailable(flag : Boolean = available) : Boolean{
            available = flag
            return available
        }
    }
}