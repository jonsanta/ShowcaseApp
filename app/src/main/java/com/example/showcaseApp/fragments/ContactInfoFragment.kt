package com.example.showcaseApp.fragments

import android.app.AlertDialog
import android.content.Intent
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ContactInfoFragmentBinding

class ContactInfoFragment: Fragment(), OnImageClickListener {
    private lateinit var viewBinding : ContactInfoFragmentBinding
    private lateinit var contactsActivity: ContactsActivity
    private lateinit var contactID : Number
    private lateinit var alertDialog: AlertDialog

    private var editMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ContactInfoFragmentBinding.inflate(layoutInflater)
        contactsActivity = requireActivity() as ContactsActivity
        val args : ContactInfoFragmentArgs by navArgs()
        contactID = args.contactID
        return viewBinding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cafBtnAdd: ImageButton = contactsActivity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = contactsActivity.findViewById(R.id.caf_btn_volver)

        cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.edit)

        val name = viewBinding.cafName
        val tel = viewBinding.cafTel
        val info = viewBinding.cafInfo
        val icon = viewBinding.cafBtnAddImage
        setEditMode(editMode, listOf(name, tel, info), icon)

        Contacts.select(contactID, name, tel, info, icon, contactsActivity.getDataBase())

        view.setOnClickListener {
            Utils.preventTwoClick(it)
            Utils.closeKeyboard(this.requireContext(), view)
            viewBinding.cafName.clearFocus()
            viewBinding.cafTel.clearFocus()
            viewBinding.cafInfo.clearFocus()
        }

        viewBinding.cafTelLinear.setOnClickListener {
            Utils.preventTwoClick(it)
            if(!editMode){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+tel.text.toString())
                startActivity(intent)
            }
        }

        viewBinding.cafBtnAddImage.setOnClickListener {
            Utils.preventTwoClick(it)
            alertDialog = Contacts.getAlertDialog(this.layoutInflater, this, this)
            alertDialog.show()
        }

        viewBinding.cafBtnBorrar.setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.getDataBase().delete("Contacts", "id='$contactID'", null)
            Navigation.findNavController(view).navigate(R.id.action_contactInfoFragment_to_contactListFragment)
        }

        cafBtnAdd.setOnClickListener {
            Utils.preventTwoClick(it)
            if(editMode){
                Contacts.update(contactID, name.text.toString(), tel.text.toString(), info.text.toString(), icon.drawable.toBitmap(), contactsActivity)
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }else{
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
            }
        }

        cafBtnVolver.setOnClickListener {
            Utils.preventTwoClick(it)
            if(!editMode)
                Navigation.findNavController(view).navigate(R.id.action_contactInfoFragment_to_contactListFragment)
            else
                swapMode(name, tel, info, icon, cafBtnAdd, cafBtnVolver)
        }
    }

    private fun swapMode(name : EditText, tel : EditText, info : EditText, icon : ImageButton, cafBtnAdd : ImageButton, cafBtnVolver : ImageButton)
    {
        setEditMode(!editMode, listOf(name, tel, info), icon)

        if(!editMode){
            cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.edit)
            cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.arrow)

            Utils.closeKeyboard(context, view)
            Contacts.select(contactID, name, tel, info, icon, contactsActivity.getDataBase())
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
        alertDialog.cancel()
    }


}