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
import androidx.fragment.app.Fragment

class ContactInfoFragment(private val cId : Int, private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)
        val cName = view.findViewById<EditText>(R.id.cName)
        val cNumber = view.findViewById<EditText>(R.id.cNumber)
        val cDesc = view.findViewById<EditText>(R.id.cDesc)

        val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = $cId", null)

        while(cursor.moveToNext()) {
            cName.setText(cursor.getString(1))
            cNumber.setText(cursor.getString(2))
            cDesc.setText(cursor.getString(3))
        }
        cursor.close()

        view.findViewById<Button>(R.id.btn_volver).setOnClickListener{
            activity.supportFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.btn_edit_contact).setOnClickListener{
            enableEditText(cName)
            enableEditText(cNumber)
            enableEditText(cDesc)
        }


        //NEEDS WHITE TEXT INSERTING CHECK
        view.findViewById<Button>(R.id.add).setOnClickListener{
            val registro = ContentValues()
            registro.put("name", cName.text.toString())//la columna nombre se rellenara con datos[0]
            registro.put("number", cNumber.text.toString().toInt())//la columna especie se rellenara con datos[1]
            registro.put("info", cDesc.text.toString())//la columna descripción se rellenara con datos[2]
            db.update("Contacts", registro, "id='$cId'", null)
            registro.clear()
            activity.supportFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.btn_del_contact).setOnClickListener{
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