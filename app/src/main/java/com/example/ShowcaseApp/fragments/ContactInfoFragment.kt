package com.example.showcaseApp.fragments

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ContactInfoFragmentBinding

class ContactInfoFragment(private val contactID : Int, private val db : SQLiteDatabase, private val activity : ContactsActivity) : Fragment(), OnImageClickListener {
    private lateinit var viewBinding : ContactInfoFragmentBinding

    private var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ContactInfoFragmentBinding.inflate(layoutInflater)

        val cafBtnAdd: ImageButton = activity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = activity.findViewById(R.id.caf_btn_volver)

        cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.edit)

        val name = viewBinding.cafName
        val tel = viewBinding.cafTel
        val info = viewBinding.cafInfo
        val icon = viewBinding.cafBtnAddImage
        setEditMode(editMode, listOf(name, tel, info), icon)

        Contacts.select(contactID, name, tel, info, icon, db)

        viewBinding.root.rootView.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Utils.closeKeyboard(viewBinding.root.rootView.context, viewBinding.root.rootView)
        }

        viewBinding.cafTelLinear.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if(!editMode){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+tel.text.toString())
                startActivity(intent)
            }
        }

        viewBinding.cafBtnAddImage.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Contacts.getAlertDialog(inflater, container, this, this).show()
        }

        viewBinding.cafBtnBorrar.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            db.delete("Contacts", "id='$contactID'", null)
            activity.supportFragmentManager.popBackStack()
        }

        cafBtnAdd.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if(editMode){
                Contacts.update(contactID, name.text.toString(), tel.text.toString(), info.text.toString(), icon.drawable.toBitmap(), db, activity)
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }else{
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }
        }

        cafBtnVolver.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if(!editMode)
                activity.supportFragmentManager.popBackStack()
            else
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
        }

        return viewBinding.root.rootView
    }

    private fun swapMode(name : EditText, tel : EditText, info : EditText, icon : ImageButton, cafBtnAdd : ImageButton, cafBtnVolver : ImageButton)
    {
        setEditMode(!editMode, listOf(name, tel, info), icon)

        if(!editMode){
            cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.edit)
            cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.arrow)

            Utils.closeKeyboard(context, view)
            Contacts.select(contactID, name, tel, info, icon, db)
        }else{
            cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.check)
            cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cancelar)
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
                    viewBinding.cafTelLinear.performClick()
                }
            }
        }
    }

    override fun onImageClick(data: Bitmap) {
        viewBinding.cafBtnAddImage.setImageBitmap(Utils.roundBitmap(data))
    }
}