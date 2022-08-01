package com.example.showcaseApp.adapters

import android.database.Cursor
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Utils

class ContactsAdapter(private var cursor : Cursor, private val contactListener: ContactListener) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    fun setCursor(c : Cursor){
        cursor = c
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val id = cursor.getString(0)
        val name = cursor.getString(1).substring(0, 1).uppercase() + cursor.getString(1).substring(1)
        val tel = cursor.getString(2)
        val info = cursor.getString(3)
        val icon = BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).size)

        holder.name.text = name
        holder.tel.text = tel
        holder.info.text = info
        holder.icon.setImageBitmap(icon)

        holder.itemView.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            cursor.close()
            contactListener.onItemClick(id.toInt())
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return cursor.count
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.ccv_name)
        val tel : TextView = itemView.findViewById(R.id.ccv_tel)
        val info : TextView = itemView.findViewById(R.id.ccv_info)
        val icon : ImageView = itemView.findViewById(R.id.ccv_icon)
    }

    interface ContactListener{
        fun onItemClick(id : Int)
    }
}