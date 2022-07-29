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

class ContactAddFragment(private val db : SQLiteDatabase, private val activity : ContactsActivity) : Fragment(),
    OnImageClickListener {

    private var edited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.contact_add_fragment, container, false)

        val cafBtnAdd: ImageButton = activity.findViewById(R.id.caf_btn_add)
        val cafBtnVolver : ImageButton = activity.findViewById(R.id.caf_btn_volver)

        cafBtnAdd.background = AppCompatResources.getDrawable(this.requireContext(),
            R.drawable.check)

        cafBtnVolver.background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cancelar)

        view.findViewById<ImageButton>(R.id.caf_btn_add_image).setOnClickListener{
            Contacts.getAlertDialog(inflater, container, this, this).show()
        }

        cafBtnAdd.setOnClickListener{
            val name = view.findViewById<EditText>(R.id.caf_name).text.toString()
            val tel = view.findViewById<EditText>(R.id.caf_tel).text.toString()
            val info = view.findViewById<EditText>(R.id.caf_info).text.toString()

            if(edited){
                val bitmap = view.findViewById<ImageButton>(R.id.caf_btn_add_image).drawable.toBitmap()
                Contacts.insert(name, tel, info, bitmap, db, activity)
            } else {
                Contacts.insert(name, tel, info, Contacts.getURLOfDrawable(R.drawable.male_avatar), db, activity)
            }
        }

        cafBtnVolver.setOnClickListener{
            activity.supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onImageClick(data: Bitmap) {
        this.activity.findViewById<ImageButton>(R.id.caf_btn_add_image).setImageBitmap(Contacts.roundBitmap(data))
        edited = true
    }
}