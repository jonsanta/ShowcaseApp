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
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.Contacts

class ContactInfoFragment(private val contactID : Int, private val db : SQLiteDatabase, private val activity : ContactListingActivity) : Fragment(), OnImageClickListener {

    private var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)

        activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = true
        activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)

        val bitmaps = Contacts.getIconList(activity)

        val name = view.findViewById<EditText>(R.id.caf_name)
        val tel = view.findViewById<EditText>(R.id.caf_tel)
        val info = view.findViewById<EditText>(R.id.caf_info)
        val icon = view.findViewById<ImageButton>(R.id.caf_btn_add_image)
        icon.isEnabled = false

        Contacts.select(contactID, name, tel, info, icon, db)

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(bitmaps, inflater, container, this, this).show()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_borrar).setOnClickListener{
            db.delete("Contacts", "id='$contactID'", null)
            activity.supportFragmentManager.popBackStack()
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
            if(editMode){
                Contacts.update(contactID, name.text.toString(), tel.text.toString(), info.text.toString(), icon.drawable.toBitmap(), db, activity)
                swapMode(name, tel, info, icon)
            }else{
                swapMode(name, tel, info, icon)
            }
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            if(!editMode) {
                activity.supportFragmentManager.popBackStack()
                activity.findViewById<ImageButton>(R.id.caf_btn_add).isVisible = false
            }
            else
                swapMode(name, tel, info, icon)
        }

        return view
    }

    private fun swapMode(name : EditText, tel : EditText, info : EditText, icon : ImageButton)
    {
        setEditMode(listOf(name, tel, info), icon)

        if(!editMode){
            activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)

            val themedValue = TypedValue()
            activity.theme.resolveAttribute(com.google.android.material.R.attr.actionModeCloseDrawable, themedValue, true)
            val drawable = AppCompatResources.getDrawable(this.requireContext(), themedValue.resourceId)

            activity.findViewById<ImageButton>(R.id.caf_btn_volver).background = drawable

            val imm = (context?.getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            Contacts.select(contactID, name, tel, info, icon, db)
        }else{
            activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(),
                R.drawable.check
            )
            activity.findViewById<ImageButton>(R.id.caf_btn_volver).background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_close_clear_cancel)
        }
    }

    //BUG - Una vez editados los campos estos puedes ser targeteados (RESULTARA KEYBOARD VISIBLE)
    private fun setEditMode(list : List<EditText>, icon : ImageButton){
        editMode = !editMode
        icon.isEnabled = !icon.isEnabled

        for(item in list)
        {
            item.isClickable = !item.isClickable
            item.isCursorVisible = !item.isCursorVisible
            item.isFocusable = !item.isFocusable
            item.isFocusableInTouchMode = !item.isFocusableInTouchMode
        }
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
    }
}