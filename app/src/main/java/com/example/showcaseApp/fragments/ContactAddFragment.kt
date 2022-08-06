package com.example.showcaseApp.fragments

import android.app.AlertDialog
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.adapters.IconListAdapter
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ContactAddFragmentBinding

//Contact adding functionality fragment
class ContactAddFragment : Fragment(), IconListAdapter.OnImageClickListener {
    private lateinit var viewBinding: ContactAddFragmentBinding
    private lateinit var contactsActivity: ContactsActivity
    private lateinit var navController: NavController
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ContactAddFragmentBinding.inflate(layoutInflater)
        contactsActivity = requireActivity() as ContactsActivity

        return viewBinding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val cafBtnAdd: ImageButton = contactsActivity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = contactsActivity.findViewById(R.id.caf_btn_volver)

        //If background clicked close Keyboard and remove focus
        view.setOnClickListener {
            Utils.preventTwoClick(it)
            Utils.closeKeyboard(context, view)
            viewBinding.cafName.clearFocus()
            viewBinding.cafTel.clearFocus()
            viewBinding.cafInfo.clearFocus()
        }

        cafBtnAdd.setImageResource(R.drawable.check)
        cafBtnVolver.setImageResource(R.drawable.cancelar)

        //Open icon selection Dialog
        viewBinding.cafBtnAddImage.setOnClickListener {
            Utils.preventTwoClick(it)
            alertDialog = Contacts.getAlertDialog(this, this)
            alertDialog.show()
        }

        //Add new contact into database
        cafBtnAdd.setOnClickListener {
            Utils.preventTwoClick(it)
            val name = viewBinding.cafName.text.toString()
            val tel = viewBinding.cafTel.text.toString()
            val info = viewBinding.cafInfo.text.toString()

            val aux : Boolean

            if(edited){
                val bitmap = viewBinding.cafBtnAddImage.drawable.toBitmap()
                aux = Contacts.insert(arrayOf(name, tel, info), bitmap, contactsActivity.getDataBase(), this.requireContext())
            } else {
                aux = Contacts.insert(arrayOf(name, tel, info), Utils.getURLOfDrawable(R.drawable.male_avatar), contactsActivity.getDataBase(), this.requireContext())
            }

            //If contact has been inserted --> True
            if(aux)
                navController.popBackStack()
        }

        cafBtnVolver.setOnClickListener {
            Utils.preventTwoClick(it)
            navController.popBackStack()
        }
    }

    override fun onImageClick(data: Bitmap) {
        viewBinding.cafBtnAddImage.setImageBitmap(Utils.roundBitmap(data))
        edited = true
        alertDialog.cancel()
    }

    companion object{
        private var edited = false
    }
}