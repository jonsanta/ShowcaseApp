package com.example.showcaseApp

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

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

            val recyclerView = RecyclerView(this.requireContext())
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
            recyclerView.adapter = IconListAdapter(Gallery.getBitmaps(false), alertDialog)

            alertDialog.setView(recyclerView)

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
                db.insert("Contacts", null, registro)

                registro.clear()
                activity.supportFragmentManager.popBackStack()
            }
        }

        return view
    }
}