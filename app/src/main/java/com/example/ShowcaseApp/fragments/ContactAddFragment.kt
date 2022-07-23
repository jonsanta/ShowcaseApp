package com.example.showcaseApp.fragments

import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.Contacts

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : ContactListingActivity) : Fragment(),
    OnImageClickListener {

    var edited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)
        activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = true
        activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(),
            R.drawable.check)


        val bitmaps = Contacts.getIconList(activity)

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(bitmaps, inflater, container, this, this).show()
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
            val name = view.findViewById<EditText>(R.id.caf_name).text.toString()
            val tel = view.findViewById<EditText>(R.id.caf_tel).text.toString()
            val info = view.findViewById<EditText>(R.id.caf_info).text.toString()

            if(edited){
                val bitmap = view.findViewById<ImageButton>(R.id.caf_btn_add_image).drawable.toBitmap()
                Contacts.insert(name, tel, info, bitmap, db, activity)
            } else
                Contacts.insert(name, tel, info, Contacts.roundBitmap(bitmaps[0]), db, activity)
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = false
            activity.supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
        edited = true
    }
}