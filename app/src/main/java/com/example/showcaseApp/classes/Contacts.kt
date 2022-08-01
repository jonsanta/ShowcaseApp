package com.example.showcaseApp.classes

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.adapters.IconListAdapter
import com.example.showcaseApp.interfaces.OnImageClickListener
import java.io.*

class Contacts {
    companion object{
        fun select(id: Number, name : EditText, tel : EditText, info : EditText, icon : ImageButton, db : SQLiteDatabase){
            val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = $id", null)

            while(cursor.moveToNext()) {
                val capitalizedName = cursor.getString(1).substring(0, 1).uppercase() + cursor.getString(1).substring(1)
                name.setText(capitalizedName)
                tel.setText(cursor.getString(2))
                info.setText(cursor.getString(3))
                icon.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).size))
            }
            cursor.close()
        }

        fun insert(name : String, tel : String, info: String, bitmap: Bitmap, activity: ContactsActivity){
            if(!checkEmpty(name, tel, activity)){
                val registry = getContentValues(name, tel, info, bitmap)
                activity.getDataBase().insert("Contacts", null, registry)
                registry.clear()
            }
        }

        fun insert(name : String, tel : String, info: String, path : String, activity: ContactsActivity){
            if(!checkEmpty(name, tel, activity)){
                val stream = activity.contentResolver.openInputStream(Uri.parse(path))
                val bitmap = BitmapFactory.decodeStream(stream)
                val registry = getContentValues(name, tel, info, Utils.roundBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, true)))
                activity.getDataBase().insert("Contacts", null, registry)
                registry.clear()
            }
        }

        fun import(list : List<String>, bitmap : Bitmap, db: SQLiteDatabase){
            val registry = getContentValues(list[0], list[1], list[2], Utils.roundBitmap(bitmap))
            db.insert("Contacts", null, registry)
            registry.clear()
        }

        fun update(id : Number, name : String, tel : String, info : String, image : Bitmap, activity : ContactsActivity){
            if(!checkEmpty(name, tel, activity)){
                val registry = getContentValues(name, tel, info, image)
                activity.getDataBase().update("Contacts", registry, "id='$id'", null)
                registry.clear()
            }
        }

        private fun checkEmpty(name : String, tel : String, activity: ContactsActivity) : Boolean{
            if(name == "" || tel == ""){
                Toast.makeText(activity.baseContext, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
                return true
            }else return false
        }

        private fun getContentValues(name : String, tel: String, info : String, image : Bitmap) : ContentValues{
            val contentValues = ContentValues()
            contentValues.put("name", name)
            contentValues.put("number", tel.toInt())
            contentValues.put("info", info)

            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)

            contentValues.put("icon", stream.toByteArray())

            return contentValues
        }

        fun getAlertDialog(inflater : LayoutInflater, fragment: Fragment, onImageClickListener: OnImageClickListener) : AlertDialog{
            val builder = AlertDialog.Builder(fragment.requireContext())
            builder.setTitle("Selecciona Imagen")

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR"){ dialog, _ ->
                dialog.dismiss()
            }

            val iconList = inflater.inflate(R.layout.icon_list, fragment.view as ViewGroup, false)

            val list = mutableListOf(Utils.getURLOfDrawable(R.drawable.male_avatar), Utils.getURLOfDrawable(R.drawable.female_avatar))

            Gallery.setPhotos(fragment.requireContext())
            Gallery.getPhotos().forEach{
                list.add(it.getThumbnail().toUri().toString())
            }

            val recyclerView = iconList.findViewById<RecyclerView>(R.id.icons_rv)
            recyclerView.layoutManager = LinearLayoutManager(alertDialog.context)
            recyclerView.adapter = IconListAdapter(list, onImageClickListener)

            alertDialog.setView(iconList)
            return alertDialog
        }

    }
}