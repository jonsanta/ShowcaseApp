package com.example.showcaseApp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactListFragment(private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment() {
    private val ids = mutableListOf<String>()
    private val names = mutableListOf<String>()
    private val tels = mutableListOf<String>()
    private val infos = mutableListOf<String>()
    private val icons = mutableListOf<Bitmap>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setConstraint(activity.findViewById<TextView>(R.id.ac2_titulo).id, 0, activity)
        var cursor = db.rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
        query(cursor)

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.clf_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactsAdapter(ids, names, tels, infos, icons, db, activity)
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
                    cursor = db.rawQuery("SELECT * FROM contacts WHERE name LIKE '" + view.findViewById<EditText>(R.id.clf_search).text.toString().uppercase() + "%' ORDER BY UPPER(name) ASC", null)

                query(cursor)
                adapter.notifyDataSetChanged()
            }
        })

        view.findViewById<ImageButton>(R.id.clf_btn_add).setOnClickListener{
            setConstraint(view!!.parent?.layoutDirection!!, 1, activity)

            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.ac2_fragment, ContactAddFragment(db, activity))
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
        icons.clear()
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0))
            names.add(cursor.getString(1))
            tels.add(cursor.getString(2))
            infos.add(cursor.getString(3))

            val bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).size)

            icons.add(bitmap)
        }
        cursor.close()
    }

    companion object {
        fun setConstraint(id : Int, mode : Int, activity : MainActivity2){
            val fragment = activity.findViewById<FrameLayout>(R.id.ac2_fragment)
            if(mode == 0)
            {
                (fragment.layoutParams as ConstraintLayout.LayoutParams).topToBottom = id
                (fragment.layoutParams as ConstraintLayout.LayoutParams).topToTop = -1
            }
            else{
                (fragment.layoutParams as ConstraintLayout.LayoutParams).topToTop = id
                (fragment.layoutParams as ConstraintLayout.LayoutParams).topToBottom = -1
            }
        }
    }

}