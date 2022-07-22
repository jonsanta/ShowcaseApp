package com.example.showcaseApp.classes

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
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
                    var bitmap = BitmapFactory.decodeResource(activity.baseContext.resources,
                        R.drawable.male_avatar
                    )
                    bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))
                    bitmap = BitmapFactory.decodeResource(activity.baseContext.resources,
                        R.drawable.female_avatar
                    )
                    bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))

                    for(bp in Gallery.getBitmaps(false).values)
                        bitmaps.add(Bitmap.createScaledBitmap(bp, 500, 500, true))
                }
            }

            return bitmaps
        }

        fun update(cId : Int, name : String, tel : String, info : String, image : ImageButton, db : SQLiteDatabase, activity : ContactListingActivity){
            if(name == "" || tel == "")
            {
                Toast.makeText(activity.baseContext, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }else {
                val registro = ContentValues()
                registro.put("name", name)//la columna nombre se rellenara con datos[0]
                registro.put("number", tel.toInt())//la columna especie se rellenara con datos[1]
                registro.put("info", info)//la columna descripción se rellenara con datos[2]

                val stream = ByteArrayOutputStream()
                val bitmap = image.drawable.toBitmap()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                registro.put("icon", stream.toByteArray())

                db.update("Contacts", registro, "id='$cId'", null)
                registro.clear()
            }
        }

        fun roundBitmap(data : Bitmap) : Bitmap{
            val output = Bitmap.createBitmap(data.width, data.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, data.width, data.height)
            val roundPx = 360f
            paint.setAntiAlias(true)
            canvas.drawRoundRect(RectF(rect), roundPx, roundPx, paint)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(data, rect, rect, paint)

            return output
        }

        fun getAlertDialog(bitmaps : List<Bitmap>, inflater : LayoutInflater, container : ViewGroup?, fragment: Fragment, onImageClickListener: OnImageClickListener) : AlertDialog{
            val builder = AlertDialog.Builder(fragment.requireContext())
            builder.setTitle("Selecciona Imagen")

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR"){ dialog, which ->
                alertDialog.dismiss()
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