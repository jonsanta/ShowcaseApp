package com.example.showcaseApp.fragments

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.adapters.ContactsAdapter
import com.example.showcaseApp.classes.AdminSQLiteOpenHelper
import com.example.showcaseApp.classes.Contacts
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.classes.XMLReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ContactListFragment(private val db : SQLiteDatabase, private val admin : AdminSQLiteOpenHelper, private val activity : ContactsActivity) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var cursor = db.rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
        activity.findViewById<ImageButton>(R.id.caf_btn_add).background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.menu)
        activity.findViewById<ImageButton>(R.id.caf_btn_volver).background = AppCompatResources.getDrawable(this.requireContext(), R.drawable.arrow)

        val view = inflater.inflate(R.layout.contact_list_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.icons_rv)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactsAdapter(cursor, db, activity)
        recyclerView.adapter = adapter

        view.findViewById<EditText>(R.id.clf_search).setOnClickListener{
            recyclerView.stopScroll()
        }

        view.findViewById<EditText>(R.id.clf_search).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                //Never used. Implementation required for addTextChangedListener
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Never used. Implementation required for addTextChangedListener
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(view.findViewById<EditText>(R.id.clf_search).text.toString() == "")
                    cursor = db.rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
                else
                    cursor = db.rawQuery("SELECT * FROM contacts WHERE name LIKE '" + view.findViewById<EditText>(
                        R.id.clf_search
                    ).text.toString().uppercase() + "%' ORDER BY UPPER(name) ASC", null)

                adapter.setCursor(cursor)
            }
        })

        view.findViewById<ImageButton>(R.id.clf_btn_add).setOnClickListener{
            activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            cursor.close()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.ac2_fragment, ContactAddFragment(db, activity))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        activity.findViewById<TextView>(R.id.ac2_export).setOnClickListener{
            activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            XMLReader.export(db, activity)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Utils.closeKeyboard(context, view)
            }
        })

        activity.findViewById<TextView>(R.id.ac2_import).setOnClickListener{
            activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/xml"

            import.launch(Intent.createChooser(intent, "Select file."))
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener{
            activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = !activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible
        }

        activity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener{
            db.close()
            admin.close()
            cursor.close()
            activity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            activity.finish()
        }

        return view
    }

    private val import = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            var inputStream : InputStream? = null

            if(uri != null)
                inputStream  = activity.contentResolver.openInputStream(uri)

            val file = Utils.copyFile(inputStream, File("${activity.getExternalFilesDir(null)}/xml/temp.xml"))
            XMLReader.import(file, db, activity)

            activity.finish()
            startActivity(activity.intent)
        }
    }
}