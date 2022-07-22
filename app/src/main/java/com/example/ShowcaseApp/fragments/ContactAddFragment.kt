package com.example.showcaseApp.fragments

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.Contacts
import java.io.ByteArrayOutputStream

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : ContactListingActivity) : Fragment(),
    OnImageClickListener {

    var edited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)
        activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = true


        val bitmaps = Contacts.getIconList(activity)

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(bitmaps, inflater, container, this, this).show()
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
            val registro = ContentValues()
            val name = view.findViewById<EditText>(R.id.caf_name)
            val tel = view.findViewById<EditText>(R.id.caf_tel)

            if(name.text.toString() == "" || tel.text.toString() == "")
            {
                Toast.makeText(activity.baseContext, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }else{
                registro.put("name", name.text.toString())
                registro.put("number",  tel.text.toString().toInt())
                registro.put("info", view.findViewById<EditText>(R.id.caf_info).text.toString())

                val stream = ByteArrayOutputStream()
                if(edited){
                    val bitmap = view.findViewById<ImageButton>(R.id.caf_btn_add_image).drawable.toBitmap()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                } else
                    Contacts.roundBitmap(bitmaps[0]).compress(Bitmap.CompressFormat.PNG, 100, stream)

                registro.put("icon", stream.toByteArray())

                db.insert("Contacts", null, registro)

                registro.clear()
                activity.supportFragmentManager.popBackStack()
            }
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
            activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = false
        }

        return view
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
        edited = true
    }
}