package com.example.showcaseApp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
import com.example.showcaseApp.adapters.ContactsAdapter
import com.example.showcaseApp.classes.Utils
import com.example.showcaseApp.classes.XMLReader
import com.example.showcaseApp.databinding.ContactListFragmentBinding
import java.io.File
import java.io.InputStream

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class ContactListFragment : Fragment(), ContactsAdapter.ContactListener{
    private lateinit var viewBinding : ContactListFragmentBinding
    private lateinit var navController: NavController

    private lateinit var contactsActivity : ContactsActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = ContactListFragmentBinding.inflate(layoutInflater)
        contactsActivity = requireActivity() as ContactsActivity

        return viewBinding.root.rootView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        var cursor = contactsActivity.getDataBase().rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)

        contactsActivity.findViewById<ImageButton>(R.id.caf_btn_add).setImageResource(R.drawable.menu)
        contactsActivity.findViewById<ImageButton>(R.id.caf_btn_volver).setImageResource(R.drawable.arrow)

        val recyclerView = viewBinding.iconsRv
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        val adapter = ContactsAdapter(cursor, this)
        recyclerView.adapter = adapter

        viewBinding.clfSearch.setOnTouchListener { v, event -> //show dialog here
            recyclerView.stopScroll()
            false
        }

        var textChanged = false

        viewBinding.clfSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Never used. Implementation required for addTextChangedListener
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(viewBinding.clfSearch.text.toString() == "")
                    cursor = contactsActivity.getDataBase().rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
                else
                    cursor = contactsActivity.getDataBase().rawQuery("SELECT * FROM contacts WHERE name LIKE '" + viewBinding.clfSearch
                        .text.toString().uppercase() + "%' ORDER BY UPPER(name) ASC", null)

                adapter.setCursor(cursor)
            }

            override fun afterTextChanged(s: Editable) {
                textChanged = true
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(!textChanged) {
                    Utils.closeKeyboard(view.context, view)
                    viewBinding.clfSearch.clearFocus()
                }
                textChanged = false
            }
        })

        viewBinding.clfBtnAdd.setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            cursor.close()

            findNavController().navigate(R.id.action_contactListFragment_to_contactAddFragment)
        }

        contactsActivity.findViewById<TextView>(R.id.ac2_export).setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            XMLReader.export(contactsActivity)
        }

        contactsActivity.findViewById<TextView>(R.id.ac2_import).setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/xml"

            import.launch(Intent.createChooser(intent, "Select file."))
        }

        contactsActivity.findViewById<ImageButton>(R.id.caf_btn_add).setOnClickListener {
            contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = !contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible
        }

        contactsActivity.findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.getDataBase().close()
            contactsActivity.getDataBaseAdmin().close()
            cursor.close()
            contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
            contactsActivity.finish()
        }
    }

    override fun onItemClick(ContactId : Int) {
        val action = ContactListFragmentDirections.actionContactListFragmentToContactInfoFragment(ContactId)
        navController.navigate(action)
        contactsActivity.findViewById<LinearLayout>(R.id.ac2_dropdown).isVisible = false
    }

    private val import = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            var inputStream : InputStream? = null

            if(uri != null)
                inputStream  = contactsActivity.contentResolver.openInputStream(uri)

            val file = Utils.copyFile(inputStream, File("${contactsActivity.getExternalFilesDir(null)}/xml/temp.xml"))
            XMLReader.import(file, contactsActivity)

            contactsActivity.finish()
            startActivity(contactsActivity.intent)
        }
    }
}