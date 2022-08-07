package com.example.showcaseApp.classes

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.IconListAdapter
import java.io.ByteArrayOutputStream


class Contacts {
    companion object{
        /** Show selected contact info in UI
         * @param id : Contact unique id
         * @param name : Contact name EditText
         * @param tel : Contact telephone number EditText
         * @param info : Contact description EditText
         * @param icon : Contact icon imageButton
         * @param db : Database instance
         */
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

        /** Insert contact with custom icon into database
         * @param data : Contact data (name, tel, info)
         * @param bitmap : Contact icon image
         * @param db : Database instance
         * @param context : Context
         * @return True: Image inserted. False: insert failed
         */
        fun insert(data : Array<String>, bitmap: Bitmap, db: SQLiteDatabase, context: Context) : Boolean{
            if(!checkEmpty(data[0], data[1], context)){
                val registry = getContentValues(data, bitmap)
                db.insert("Contacts", null, registry)
                registry.clear()
                return true
            }else
                return false
        }

        /** Insert contact with default icon into database
         * @param data : Contact data (name, tel, info)
         * @param path : Drawable path
         * @param db : Database instance
         * @param context : Context
         * @return True: Image inserted. False: insert failed
         */
        fun insert(data : Array<String>, path : String, db: SQLiteDatabase, context: Context) : Boolean{
            if(!checkEmpty(data[0], data[1], context)){
                val stream = context.contentResolver.openInputStream(Uri.parse(path))
                val bitmap = BitmapFactory.decodeStream(stream)
                val registry = getContentValues(data, Utils.roundBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, true)))
                db.insert("Contacts", null, registry)
                registry.clear()
                return true
            }else
                return false
        }

        /** Insert XML contact into database
         * @param data : Contact data (name, tel, info)
         * @param bitmap : Contact icon image
         * @param db : Database instance
         */
        fun import(data : Array<String>, bitmap : Bitmap, db: SQLiteDatabase){
            val registry = getContentValues(data, Utils.roundBitmap(bitmap))
            db.insert("Contacts", null, registry)
            registry.clear()
        }

        /** Update given contact
         * @param data : Contact data (id, name, tel, info)
         * @param bitmap : Contact icon image
         * @param db : Database instance
         * @param context : Context
         */
        fun update(data : Array<String>, bitmap : Bitmap, db : SQLiteDatabase, context: Context){
            if(!checkEmpty(data[1], data[2], context)){
                val registry = getContentValues(arrayOf(data[1], data[2], data[3]), bitmap)
                db.update("Contacts", registry, "id='${data[0].toInt()}'", null)
                registry.clear()
            }
        }

        /** Insert XML contact into database
         * @param name : Contact name
         * @param tel : Contact telephone
         * @param context : Context
         * @return name or telephone editText empty
         */
        private fun checkEmpty(name : String, tel : String, context: Context) : Boolean{
            if(name == "" || tel == ""){
                Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
                return true
            }else return false
        }

        /** build ContentValues with received contact data
         * @param data : Contact data (name, tel, info)
         * @param bitmap : Contact icon image
         * @return ContactValues containing contact data
         */
        private fun getContentValues(data : Array<String>, bitmap : Bitmap) : ContentValues{
            val contentValues = ContentValues()
            contentValues.put("name", data[0])
            contentValues.put("number", data[1].toInt())
            contentValues.put("info", data[2])

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

            contentValues.put("icon", stream.toByteArray())

            return contentValues
        }

        /** Inflates AlertDialog for contact icon selection
         * @param fragment : Fragment that will inflate AlertDialog and receive data
         * @param onImageClickListener : AlertDialog item Click Listener
         * @return inflated AlertDialog
         */
        fun getAlertDialog(fragment: Fragment, onImageClickListener: IconListAdapter.OnImageClickListener) : AlertDialog{
            val builder = AlertDialog.Builder(fragment.requireContext())
            val title = TextView(fragment.requireContext())
            title.text = fragment.resources.getString(R.string.ad_titulo_alert_dialog)
            title.setPadding(0, 30, 0, 30)
            title.gravity = Gravity.CENTER
            //title.setTypeface(Typeface.DEFAULT_BOLD)
            //title.setTextColor(Color.BLACK)
            title.textSize = 23f

            builder.setCustomTitle(title)

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, fragment.resources.getText(R.string.ad_btn_cancelar)){ dialog, _ ->
                dialog.dismiss()
            }

            val iconList = fragment.layoutInflater.inflate(R.layout.icon_list, fragment.view as ViewGroup, false)

            //Add default icons
            val list = mutableListOf(Utils.getURLOfDrawable(R.drawable.male_avatar), Utils.getURLOfDrawable(R.drawable.female_avatar))

            //Add gallery photos to icon list
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