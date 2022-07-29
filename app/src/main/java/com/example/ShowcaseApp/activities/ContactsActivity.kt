package com.example.showcaseApp.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.showcaseApp.classes.AdminSQLiteOpenHelper
import com.example.showcaseApp.fragments.ContactListFragment
import com.example.showcaseApp.R

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts_activity)

        val admin = AdminSQLiteOpenHelper(this, "Contacts", null, 1)
        val db : SQLiteDatabase = admin.writableDatabase

        supportFragmentManager.beginTransaction().add(R.id.ac2_fragment, ContactListFragment(db, admin,this)).commit()
    }
}