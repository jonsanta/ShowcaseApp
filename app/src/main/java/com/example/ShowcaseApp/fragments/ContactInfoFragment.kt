package com.example.showcaseApp.fragments

import android.app.Activity
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.Contacts

class ContactInfoFragment(private val cId : Int, private val db : SQLiteDatabase, private val activity : ContactListingActivity) : Fragment(), OnImageClickListener {

    private var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)
        activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = true

        val bitmaps = Contacts.getIconList(activity)

        val name = view.findViewById<EditText>(R.id.caf_name)
        val tel = view.findViewById<EditText>(R.id.caf_tel)
        val info = view.findViewById<EditText>(R.id.caf_info)
        val icon = view.findViewById<ImageButton>(R.id.caf_btn_add_image)
        icon.isEnabled = false

        loadData(name, tel, info, icon)

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(bitmaps, inflater, container, this, this).show()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_borrar).setOnClickListener{
            db.delete("Contacts", "id='$cId'", null)
            activity.supportFragmentManager.popBackStack()
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
            if(editMode){
                Contacts.update(cId, name.text.toString(), tel.text.toString(), info.text.toString(), icon, db, activity)
                check(name, tel, info, icon)
            }else{
                check(name, tel, info, icon)
            }
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            if(!editMode) {
                activity.supportFragmentManager.popBackStack()
                activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = false
            }
            else
                check(name, tel, info, icon)
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
            activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)

            val themedValue = TypedValue()
            activity.theme.resolveAttribute(com.google.android.material.R.attr.actionModeCloseDrawable, themedValue, true)
            val drawable = AppCompatResources.getDrawable(this.requireContext(), themedValue.resourceId)

            activity.findViewById<ImageButton>(R.id.caf_btn_volver).background = drawable

            val imm = (context?.getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            icon.isEnabled = false
            loadData(name, tel, info, icon)
        }else{
            activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(),
                R.drawable.check
            )
            activity.findViewById<ImageButton>(R.id.caf_btn_volver).background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_close_clear_cancel)
            icon.isEnabled = true
        }
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
    }
}