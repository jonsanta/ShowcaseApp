package com.example.showcaseApp.classes

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.adapters.IconListAdapter
import com.example.showcaseApp.interfaces.OnImageClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class Contacts {
    companion object{
        fun getIconList (activity: ContactListingActivity) : MutableList<Bitmap>{
            val bitmaps = mutableListOf<Bitmap>()
            activity.lifecycleScope.launch { // Generate Bitmaps
                withContext(Dispatchers.IO) {
                    bitmaps.add(
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                            activity.baseContext.resources,
                            R.drawable.male_avatar
                        ), 500, 500, true)
                    )

                    bitmaps.add(
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                            activity.baseContext.resources,
                            R.drawable.female_avatar
                        ), 500, 500, true)
                    )

                    for(bitmap in Gallery.getBitmaps(false).values)
                        bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))
                }
            }

            return bitmaps
        }

        fun select(id: Int, name : EditText, tel : EditText, info : EditText, icon : ImageButton, db : SQLiteDatabase){
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

        fun insert(name : String, tel : String, info: String, image : Bitmap, db : SQLiteDatabase, activity: ContactListingActivity){
            if(!checkEmpty(name, tel, activity)){
                val registry = getContentValues(name, tel, info, image)
                db.insert("Contacts", null, registry)
                registry.clear()
                activity.supportFragmentManager.popBackStack()
            }
        }

        fun import(list : List<String>, bitmap : Bitmap, db: SQLiteDatabase){
            val registry = getContentValues(list[0], list[1], list[2], roundBitmap(bitmap))
            db.insert("Contacts", null, registry)
            registry.clear()
        }

        fun update(id : Int, name : String, tel : String, info : String, image : Bitmap, db : SQLiteDatabase, activity : ContactListingActivity){
            if(!checkEmpty(name, tel, activity)){
                val registry = getContentValues(name, tel, info, image)
                db.update("Contacts", registry, "id='$id'", null)
                registry.clear()
            }
        }

        private fun checkEmpty(name : String, tel : String, activity: ContactListingActivity) : Boolean{
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

        fun roundBitmap(data : Bitmap) : Bitmap{
            val output = Bitmap.createBitmap(data.width, data.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, data.width, data.height)
            val roundPx = 360f
            paint.isAntiAlias = true
            canvas.drawRoundRect(RectF(rect), roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(data, rect, rect, paint)

            return output
        }

        fun getAlertDialog(bitmaps : List<Bitmap>, inflater : LayoutInflater, container : ViewGroup?, fragment: Fragment, onImageClickListener: OnImageClickListener) : AlertDialog{
            val builder = AlertDialog.Builder(fragment.requireContext())
            builder.setTitle("Selecciona Imagen")

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR"){ dialog, _ ->
                dialog.dismiss()
            }

            val iconList = inflater.inflate(R.layout.icon_list, container, false)

            val recyclerView = iconList.findViewById<RecyclerView>(R.id.icons_rv)
            recyclerView.layoutManager = LinearLayoutManager(alertDialog.context)
            recyclerView.adapter = IconListAdapter(bitmaps, alertDialog, onImageClickListener)

            alertDialog.setView(iconList)
            return alertDialog
        }

    }
}