package com.example.showcaseApp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class ContactInfoFragment(private val cId : Int, private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)

        System.out.println(cId)
        val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = "+cId+"" , null)

        while(cursor.moveToNext()) {
            view.findViewById<EditText>(R.id.cName).setText(cursor.getString(1))
            view.findViewById<EditText>(R.id.cNumber).setText(cursor.getString(2))
            view.findViewById<EditText>(R.id.cDesc).setText(cursor.getString(3))
        }

        view.findViewById<Button>(R.id.btn_volver_add).setOnClickListener{
            activity.supportFragmentManager.popBackStack();
        }

        return view

    }
}