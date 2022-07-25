package com.example.showcaseApp.fragments

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import com.example.showcaseApp.classes.Contacts

class ContactInfoFragment(private val contactID : Int, private val db : SQLiteDatabase, private val activity : ContactListingActivity) : Fragment(), OnImageClickListener {

    private var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_info_fragment, container, false)

        val cafBtnAdd: ImageButton = activity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = activity.findViewById(R.id.caf_btn_volver)

        cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)

        val bitmaps = Contacts.getIconList(activity)

        val name = view.findViewById<EditText>(R.id.caf_name)
        val tel = view.findViewById<EditText>(R.id.caf_tel)
        val info = view.findViewById<EditText>(R.id.caf_info)
        val icon = view.findViewById<ImageButton>(R.id.caf_btn_add_image)
        setEditMode(editMode, listOf(name, tel, info), icon)

        Contacts.select(contactID, name, tel, info, icon, db)

        view.findViewById<LinearLayout>(R.id.caf_tel_linear).setOnClickListener{
            if(!editMode){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+tel.text.toString())
                startActivity(intent)
            }
        }

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(bitmaps, inflater, container, this, this).show()
        }

        view.findViewById<ImageButton>(R.id.caf_btn_borrar).setOnClickListener{
            db.delete("Contacts", "id='$contactID'", null)
            activity.supportFragmentManager.popBackStack()
        }

        cafBtnAdd.setOnClickListener{
            if(editMode){
                Contacts.update(contactID, name.text.toString(), tel.text.toString(), info.text.toString(), icon.drawable.toBitmap(), db, activity)
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }else{
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }
        }

        cafBtnVolver.setOnClickListener{
            if(!editMode)
                activity.supportFragmentManager.popBackStack()
            else
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
        }

        return view
    }

    private fun swapMode(name : EditText, tel : EditText, info : EditText, icon : ImageButton, cafBtnAdd : ImageButton, cafBtnVolver : ImageButton)
    {
        setEditMode(!editMode, listOf(name, tel, info), icon)

        if(!editMode){
            cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_edit)
            cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.arrow)

            val imm = (context?.getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            Contacts.select(contactID, name, tel, info, icon, db)
        }else{
            cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.check)
            cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), android.R.drawable.ic_menu_close_clear_cancel)
        }
    }

    private fun setEditMode(flag : Boolean, list : List<EditText>, icon : ImageButton){
        editMode = flag
        icon.isEnabled = flag

        val types = listOf(InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_PHONE, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        for(i in list.indices){
            list[i].isFocusable = flag
            list[i].isFocusableInTouchMode = flag
            list[i].isClickable = flag
            if(flag)
                list[i].inputType = types[i]
            else {
                list[i].inputType = InputType.TYPE_NULL
                list[1].setOnClickListener{
                    view?.findViewById<LinearLayout>(R.id.caf_tel_linear)?.performClick()
                }
            }
        }
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
    }
}