package com.example.showcaseApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ContactsAdapter(private val names : List<String>, private val tels : List<String>, private val infos : List<String>, private val activity : MainActivity2) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact, parent, false)

        view.setOnClickListener{
            val transaction = activity.supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment, ContactInfoFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = names.get(position)
        holder.tel.text = tels.get(position)
        holder.info.text = infos.get(position)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return names.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val tel : TextView = itemView.findViewById(R.id.tel)
        val info : TextView = itemView.findViewById(R.id.info)
    }
}