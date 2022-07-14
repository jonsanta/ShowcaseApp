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

class ContactInfoFragment(private val cId : Int, private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)
        val name = view.findViewById<EditText>(R.id.caf_name)
        val tel = view.findViewById<EditText>(R.id.caf_tel)
        val info = view.findViewById<EditText>(R.id.caf_info)

        val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = $cId", null)

        while(cursor.moveToNext()) {
            name.setText(cursor.getString(1))
            tel.setText(cursor.getString(2))
            info.setText(cursor.getString(3))
        }
        cursor.close()

        view.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_edit).setOnClickListener{
            enableEditText(name)
            enableEditText(tel)
            enableEditText(info)
        }


        view.findViewById<Button>(R.id.caf_btn_add).setOnClickListener{
            if(name.text.toString() == "" || tel.text.toString() == "")
            {
                Toast.makeText(activity.baseContext, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }else {
                val registro = ContentValues()
                registro.put("name", name.text.toString())//la columna nombre se rellenara con datos[0]
                registro.put("number", tel.text.toString().toInt())//la columna especie se rellenara con datos[1]
                registro.put("info", info.text.toString())//la columna descripción se rellenara con datos[2]
                db.update("Contacts", registro, "id='$cId'", null)
                registro.clear()
                activity.supportFragmentManager.popBackStack()
            }
        }

        view.findViewById<ImageButton>(R.id.caf_btn_del).setOnClickListener{
            db.delete("Contacts", "id='$cId'", null)
            activity.supportFragmentManager.popBackStack()
        }

        return view

    }

    private fun enableEditText(view : EditText){
        view.isClickable = true
        view.isCursorVisible = true
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }
}