package com.example.showcaseApp

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment(), OnImageClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)

        view.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Selecciona Imagen")

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR"){ dialog, which ->
                alertDialog.dismiss()
            }

            val iconList = inflater.inflate(R.layout.icon_list, container, false)

            val bitmaps = mutableListOf<Bitmap>()

            for(bitmap in Gallery.getBitmaps(false).values)
                bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))

            val recyclerView = iconList.findViewById<RecyclerView>(R.id.icons_rv)
            recyclerView.layoutManager = LinearLayoutManager(alertDialog.context)
            recyclerView.adapter = IconListAdapter(bitmaps, alertDialog, this)

            alertDialog.setView(iconList)

            alertDialog.show()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
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
                val bitmap = view.findViewById<ImageButton>(R.id.caf_btn_add_image).drawable.toBitmap()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                registro.put("icon", stream.toByteArray())

                db.insert("Contacts", null, registro)

                registro.clear()
                activity.supportFragmentManager.popBackStack()
            }
        }

        return view
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(data)
    }
}