package com.example.showcaseApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

public class ContactListFragment(private val list : List<String>, private val activity : MainActivity2) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.contacts)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = ContactsAdapter(list, activity)

        view.findViewById<ImageButton>(R.id.addContact).setOnClickListener{
            val transaction = activity.supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment, ContactAddFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}