package com.example.showcaseApp


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ContactInfoFragment(private val cId : Int, private val db : SQLiteDatabase, private val activity : MainActivity2) : Fragment(), OnImageClickListener {

    var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)

        val bitmaps = mutableListOf<Bitmap>()
        lifecycleScope.launch { // Generate Bitmaps
            withContext(Dispatchers.IO) {
                var bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.male_avatar)
                bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))
                bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.female_avatar)
                bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))
            }
        }

        val name = view.findViewById<EditText>(R.id.caf_name)
        val tel = view.findViewById<EditText>(R.id.caf_tel)
        val info = view.findViewById<EditText>(R.id.caf_info)
        val icon = view.findViewById<ImageButton>(R.id.caf_btn_add_image)
        icon.isEnabled = false

        loadData(name, tel, info, icon)

        view.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            if(!editMode)
                activity.supportFragmentManager.popBackStack()
            else
                check(name, tel, info, icon)
        }

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Selecciona Imagen")

            val alertDialog = builder.create()

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR"){ dialog, which ->
                alertDialog.dismiss()
            }

            val iconList = inflater.inflate(R.layout.icon_list, container, false)

            for(bitmap in Gallery.getBitmaps(false).values)
                bitmaps.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true))

            val recyclerView = iconList.findViewById<RecyclerView>(R.id.icons_rv)
            recyclerView.layoutManager = LinearLayoutManager(alertDialog.context)
            recyclerView.adapter = IconListAdapter(bitmaps, alertDialog, this)

            alertDialog.setView(iconList)

            alertDialog.show()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_edit).setOnClickListener{
            if(editMode){
                if(name.text.toString() == "" || tel.text.toString() == "")
                {
                    Toast.makeText(activity.baseContext, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
                }else {
                    val registro = ContentValues()
                    registro.put("name", name.text.toString())//la columna nombre se rellenara con datos[0]
                    registro.put("number", tel.text.toString().toInt())//la columna especie se rellenara con datos[1]
                    registro.put("info", info.text.toString())//la columna descripción se rellenara con datos[2]

                    val stream = ByteArrayOutputStream()
                    val bitmap = view.findViewById<ImageButton>(R.id.caf_btn_add_image).drawable.toBitmap()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    registro.put("icon", stream.toByteArray())

                    db.update("Contacts", registro, "id='$cId'", null)
                    registro.clear()
                    check(name, tel, info, icon)
                }
            }else{
                check(name, tel, info, icon)
            }
        }


        view.findViewById<ImageButton>(R.id.caf_btn_borrar).setOnClickListener{
            db.delete("Contacts", "id='$cId'", null)
            activity.supportFragmentManager.popBackStack()
        }


        return view

    }

    private fun loadData(name : EditText, tel : EditText, info : EditText, icon : ImageButton){
        val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = $cId", null)

        while(cursor.moveToNext()) {
            name.setText(cursor.getString(1))
            tel.setText(cursor.getString(2))
            info.setText(cursor.getString(3))
            icon.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).size))
        }
        cursor.close()
    }

    //BUG - Una vez editados los campos estos puedes ser targeteados (RESULTARA KEYBOARD VISIBLE)
    private fun enableEditText(view : EditText){
        view.isClickable = !view.isClickable
        view.isCursorVisible = !view.isCursorVisible
        view.isFocusable = !view.isFocusable
        view.isFocusableInTouchMode = !view.isFocusableInTouchMode
    }

    private fun check(name : EditText, tel : EditText, info : EditText, icon : ImageButton)
    {
        editMode = !editMode
        enableEditText(name)
        enableEditText(tel)
        enableEditText(info)

        if(!editMode){
            view?.findViewById<ImageButton>(R.id.caf_btn_edit)?.background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)

            val themedValue = TypedValue()
            activity.theme.resolveAttribute(com.google.android.material.R.attr.actionModeCloseDrawable, themedValue, true)
            val drawable = AppCompatResources.getDrawable(this.requireContext(), themedValue.resourceId)

            view?.findViewById<ImageButton>(R.id.caf_btn_volver)?.background = drawable

            val imm = (context?.getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            icon.isEnabled = false
            loadData(name, tel, info, icon)
        }else{
            view?.findViewById<ImageButton>(R.id.caf_btn_edit)?.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.check)
            view?.findViewById<ImageButton>(R.id.caf_btn_volver)?.background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_close_clear_cancel)
            icon.isEnabled = true
        }
    }

    override fun onImageClick(data: Bitmap) {
        //ROUNDED CORNER BITMAP
        val output = Bitmap.createBitmap(data.width, data.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, data.width, data.height)
        val roundPx = 360f
        paint.setAntiAlias(true)
        canvas.drawRoundRect(RectF(rect), roundPx, roundPx, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(data, rect, rect, paint)

        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(output)
    }
}