package com.example.showcaseApp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)

        view.findViewById<Button>(R.id.btn_volver_add).setOnClickListener{
            activity.supportFragmentManager.popBackStack();
        }

        view.findViewById<Button>(R.id.add).setOnClickListener{
            val registro = ContentValues()
            val nameView = view.findViewById<EditText>(R.id.cName)
            val telView = view.findViewById<EditText>(R.id.cNumber)

            if(nameView.text.toString() == "" || telView.text.toString() == "")
            {
                System.out.println("Faltan campos por completar")
            }else{
                registro.put("name", nameView.text.toString())
                registro.put("number",  telView.text.toString().toInt())
                registro.put("info", view.findViewById<EditText>(R.id.cDesc).text.toString())
                db.insert("Contacts", null, registro)

                registro.clear()
                activity.supportFragmentManager.popBackStack();
            }
        }

        return view
    }
}