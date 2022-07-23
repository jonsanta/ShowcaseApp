package com.example.showcaseApp.fragments

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.adapters.ContactsAdapter
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.AdminSQLiteOpenHelper


class ContactListFragment(private val db : SQLiteDatabase, private val admin : AdminSQLiteOpenHelper, private val activity : ContactListingActivity) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var cursor = db.rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.icons_rv)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactsAdapter(cursor, db, activity)
        recyclerView.adapter = adapter

        view.findViewById<EditText>(R.id.clf_search).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(view.findViewById<EditText>(R.id.clf_search).text.toString() == "")
                    cursor = db.rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
                else
                    cursor = db.rawQuery("SELECT * FROM contacts WHERE name LIKE '" + view.findViewById<EditText>(
                        R.id.clf_search
                    ).text.toString().uppercase() + "%' ORDER BY UPPER(name) ASC", null)

                adapter.setCursor(cursor)
            }
        })

        view.findViewById<ImageButton>(R.id.clf_btn_add).setOnClickListener{
            cursor.close()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.ac2_fragment, ContactAddFragment(db, activity))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            db.close()
            admin.close()
            cursor.close()
            activity.finish()
        }

        return view
    }
}