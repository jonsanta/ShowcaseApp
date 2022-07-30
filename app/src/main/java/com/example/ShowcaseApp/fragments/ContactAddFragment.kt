package com.example.showcaseApp.fragments

import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.showcaseApp.interfaces.OnImageClickListener
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.databinding.ContactAddFragmentBinding

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : ContactsActivity) : Fragment(), OnImageClickListener {
    private lateinit var viewBinding: ContactAddFragmentBinding

    private var edited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ContactAddFragmentBinding.inflate(layoutInflater)

        val cafBtnAdd: ImageButton = activity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = activity.findViewById(R.id.caf_btn_volver)

        viewBinding.root.rootView.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Utils.closeKeyboard(context, view)
        }

        cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(),
            R.drawable.check)

        cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cancelar)

        viewBinding.cafBtnAddImage.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            Contacts.getAlertDialog(inflater, container, this, this).show()
        }

        cafBtnAdd.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            val name = viewBinding.cafName.text.toString()
            val tel = viewBinding.cafTel.text.toString()
            val info = viewBinding.cafInfo.text.toString()

            if(edited){
                val bitmap = viewBinding.cafBtnAddImage.drawable.toBitmap()
                Contacts.insert(name, tel, info, bitmap, db, activity)
            } else {
                Contacts.insert(name, tel, info, Utils.getURLOfDrawable(R.drawable.male_avatar), db, activity)
            }
        }

        cafBtnVolver.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            activity.supportFragmentManager.popBackStack()
        }

        return viewBinding.root.rootView
    }

    override fun onImageClick(data: Bitmap) {
        viewBinding.cafBtnAddImage.setImageBitmap(Utils.roundBitmap(data))
        edited = true
    }
}