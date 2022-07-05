package com.example.showcaseApp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactListFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {
    val names = mutableListOf<String>()
    val tels = mutableListOf<String>()
    val infos = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val cursor = db.rawQuery("SELECT * FROM contacts" , null)
        query(cursor)

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.contacts)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactsAdapter(names, tels, infos, activity)
        recyclerView.adapter = adapter

        view.findViewById<EditText>(R.id.search).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val cursor = db.rawQuery(
                    "SELECT * FROM contacts WHERE name LIKE '" + view.findViewById<EditText>(R.id.search).text.toString()
                        .uppercase() + "%'", null
                )
                query(cursor)
                adapter.notifyDataSetChanged()
            }
        })

        view.findViewById<ImageButton>(R.id.addContact).setOnClickListener{
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, ContactAddFragment(db, activity))
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

    fun query(cursor : Cursor){
        names.clear()
        tels.clear()
        infos.clear()
        while (cursor.moveToNext()) {
            names.add(cursor.getString(1))
            tels.add(cursor.getString(2))
            infos.add(cursor.getString(3))
        }
    }
}