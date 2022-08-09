package com.example.showcaseApp.adapters

import android.database.Cursor
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.showcaseApp.R
import com.example.showcaseApp.classes.Utils

/** Contact list RecyclerView adapter
 * @param cursor : cursor containing all selected contacts
 * @param contactListener : Contact item click Listener
 */
class ContactsAdapter(private var cursor : Cursor, private val contactListener: ContactListener) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    //Refresh contact RecyclerView list
    fun setCursor(c : Cursor){
        cursor = c
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //set cursor position & get his data
        cursor.moveToPosition(position)
        val id = cursor.getString(0)
        val name = cursor.getString(1).substring(0, 1).uppercase() + cursor.getString(1).substring(1)
        val tel = cursor.getString(2)
        val icon = BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).size)

        holder.name.text = name
        holder.tel.text = tel
        Glide.with(holder.itemView).load(icon).apply(
            RequestOptions
            .bitmapTransform(RoundedCorners(360)))
            .into(holder.icon)

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
        val icon : ImageView = itemView.findViewById(R.id.ccv_icon)
    }

    interface ContactListener{
        fun onItemClick(contactId : Int)
    }
}