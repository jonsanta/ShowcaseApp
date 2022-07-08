package com.example.showcaseApp

import android.app.ActionBar
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactListFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {
    val ids = mutableListOf<String>()
    val names = mutableListOf<String>()
    val tels = mutableListOf<String>()
    val infos = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setConstraint(activity.findViewById<TextView>(R.id.a2Title).id, 0, activity)
        val cursor = db.rawQuery("SELECT * FROM contacts" , null)
        query(cursor)

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.contacts)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactsAdapter(ids, names, tels, infos, db, activity)
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
            setConstraint(view!!.parent?.layoutDirection!!, 1, activity)

            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, ContactAddFragment(db, activity))
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

    fun query(cursor : Cursor){
        ids.clear()
        names.clear()
        tels.clear()
        infos.clear()
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0))
            names.add(cursor.getString(1))
            tels.add(cursor.getString(2))
            infos.add(cursor.getString(3))
        }
    }

    companion object {
        fun setConstraint(id : Int, mode : Int, activity : MainActivity2){
            if(mode == 0)
            {
                (activity.findViewById<FrameLayout>(R.id.fragment).layoutParams as ConstraintLayout.LayoutParams).topToBottom = id
                (activity.findViewById<FrameLayout>(R.id.fragment).layoutParams as ConstraintLayout.LayoutParams).topToTop = -1
            }
            else{
                (activity.findViewById<FrameLayout>(R.id.fragment).layoutParams as ConstraintLayout.LayoutParams).topToTop = id
                (activity.findViewById<FrameLayout>(R.id.fragment).layoutParams as ConstraintLayout.LayoutParams).topToBottom = -1
            }
        }
    }

}