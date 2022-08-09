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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

//Initial Contact Activity Fragment - Contact Listing
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

        //Show all contacts on activity start
        var cursor = contactsActivity.getDataBase().rawQuery("SELECT * FROM contacts ORDER BY UPPER(name) ASC" , null)
        contactsActivity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appBG)

        //populate RecyclerView with cursor
        val recyclerView = viewBinding.iconsRv
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        val adapter = ContactsAdapter(cursor, this)
        recyclerView.adapter = adapter


        viewBinding.clfSearch.setOnTouchListener { _, _ ->
            recyclerView.stopScroll()
            false
        }

        var textChanged = false

        //If SearchBar editText is edited, makes new db Select.
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

        //Close EditText when contact list is scrolled
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(!textChanged) {
                    Utils.closeKeyboard(view.context, view)
                    viewBinding.clfSearch.clearFocus()
                }
                textChanged = false
            }
        })

        //Close EditText if touch outside
        viewBinding.root.setOnTouchListener{ _, _ ->
            Utils.closeKeyboard(view.context, view)
            viewBinding.clfSearch.clearFocus()
            false
        }

        //Close EditText if touch outside
        recyclerView.setOnTouchListener{ _, _ ->
            Utils.closeKeyboard(view.context, view)
            viewBinding.clfSearch.clearFocus()
            false
        }

        //Open ContactAdd Fragment
        viewBinding.clfBtnAdd.setOnClickListener {
            Utils.preventTwoClick(it)
            viewBinding.clfDropdown.isVisible = false
            cursor.close()

            findNavController().navigate(R.id.action_contactListFragment_to_contactAddFragment)
        }

        //Export actual contacts into XML file
        viewBinding.clfExport.setOnClickListener {
            Utils.preventTwoClick(it)
            viewBinding.clfDropdown.isVisible = false
            XMLReader.export(contactsActivity.getDataBase(), this.requireContext())
        }

        //Import selected XML file as new Contacts
        viewBinding.clfImport.setOnClickListener {
            Utils.preventTwoClick(it)
            viewBinding.clfDropdown.isVisible = false
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/xml"

            import.launch(Intent.createChooser(intent, "Select file."))
        }

        viewBinding.clfMenu.setOnClickListener {
            viewBinding.clfDropdown.isVisible = !viewBinding.clfDropdown.isVisible
        }

        //Close Activity
        viewBinding.clfBtnVolver.setOnClickListener {
            Utils.preventTwoClick(it)
            contactsActivity.getDataBase().close()
            contactsActivity.getDataBaseAdmin().close()
            cursor.close()
            viewBinding.clfDropdown.isVisible = false
            contactsActivity.finish()
        }
    }

    override fun onItemClick(contactId : Int) {
        val action = ContactListFragmentDirections.actionContactListFragmentToContactInfoFragment(contactId)
        navController.navigate(action)
        viewBinding.clfDropdown.isVisible = false
    }

    //Launch File selection Dialog
    private val import = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //Receive Content scheme Uri
            val uri: Uri? = result.data?.data
            var inputStream : InputStream? = null

            if(uri != null)
                inputStream  = contactsActivity.contentResolver.openInputStream(uri)

            //Write content scheme Uri into new file & load his content
            val file = Utils.copyFile(inputStream, File("${contactsActivity.getExternalFilesDir(null)}/xml/temp.xml"))
            XMLReader.import(file, contactsActivity.getDataBase(), this.requireContext())

            contactsActivity.finish()
            startActivity(contactsActivity.intent)
        }
    }
}