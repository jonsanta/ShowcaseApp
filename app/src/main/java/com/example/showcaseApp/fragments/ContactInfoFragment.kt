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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.adapters.IconListAdapter
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ContactInfoFragmentBinding

//Contact info showing functionality fragment
class ContactInfoFragment: Fragment(), IconListAdapter.OnImageClickListener {
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
        contactsActivity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.light_gray)

        viewBinding.cifBtnAdd.setImageResource(R.drawable.edit)

        val name = viewBinding.cafName
        val tel = viewBinding.cafTel
        val info = viewBinding.cafInfo
        val icon = viewBinding.cafBtnAddImage
        //Disable editing mode
        setEditMode(editMode, listOf(name, tel, info), icon)

        //Load contact data
        Contacts.select(contactID, name, tel, info, icon, contactsActivity.getDataBase())

        //If background clicked close Keyboard and remove focus
        view.setOnClickListener {
            Utils.preventTwoClick(it)
            Utils.closeKeyboard(this.requireContext(), view)
            viewBinding.cafName.clearFocus()
            viewBinding.cafTel.clearFocus()
            viewBinding.cafInfo.clearFocus()
        }

        //Open Telephone Dial
        viewBinding.cafTelLinear.setOnClickListener {
            Utils.preventTwoClick(it)
            if(!editMode){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+tel.text.toString())
                startActivity(intent)
            }
        }

        //Open icon selection Dialog
        viewBinding.cafBtnAddImage.setOnClickListener {
            Utils.preventTwoClick(it)
            alertDialog = Contacts.getAlertDialog(this, this)
            alertDialog.show()
        }

        //Delete contact
        viewBinding.cafBtnBorrar.setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.getDataBase().delete("Contacts", "id='$contactID'", null)
            Navigation.findNavController(view).popBackStack()
        }

        //swap between editmode True - False & Updates database contact data
        viewBinding.cifBtnAdd.setOnClickListener {
            Utils.preventTwoClick(it)
            if(editMode){
                Contacts.update(arrayOf(contactID.toString(), name.text.toString(), tel.text.toString(), info.text.toString()), icon.drawable.toBitmap(), contactsActivity.getDataBase(), this.requireContext())
                swapMode(name, tel, info, icon, viewBinding.cifBtnAdd, viewBinding.cifBtnVolver)
            }else{
                swapMode(name, tel, info, icon, viewBinding.cifBtnAdd, viewBinding.cifBtnVolver)
            }
        }

        viewBinding.cifBtnVolver.setOnClickListener {
            Utils.preventTwoClick(it)
            if(!editMode)
                Navigation.findNavController(view).popBackStack()
            else
                swapMode(name, tel, info, icon, viewBinding.cifBtnAdd, viewBinding.cifBtnVolver)
        }
    }

    //Enable - Disable EditMode UI
    private fun swapMode(name : EditText, tel : EditText, info : EditText, icon : ImageButton, cafBtnAdd : ImageButton, cafBtnVolver : ImageButton)
    {
        setEditMode(!editMode, listOf(name, tel, info), icon)

        if(!editMode){
            cafBtnAdd.setImageResource(R.drawable.edit)
            cafBtnVolver.setImageResource(R.drawable.arrow)

            Utils.closeKeyboard(context, view)
            Contacts.select(contactID, name, tel, info, icon, contactsActivity.getDataBase())
        }else{
            cafBtnAdd.setImageResource(R.drawable.check)
            cafBtnVolver.setImageResource(R.drawable.cancelar)
        }
    }

    //Enable - Disable EditMode UI
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