package com.example.showcaseApp.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.showcaseApp.databinding.CameraActivityBinding
import com.example.showcaseApp.fragments.CameraFragment

typealias LumaListener = (luma: Double) -> Unit

class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: CameraActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}