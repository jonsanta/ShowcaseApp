package com.example.showcaseApp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)

        view.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.caf_btn_add).setOnClickListener{
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