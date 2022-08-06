package com.example.showcaseApp.activities

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.showcaseApp.classes.AdminSQLiteOpenHelper
import com.example.showcaseApp.databinding.ContactsActivityBinding

/** Contact Listing Activity
* Contains Database instance
*/
class ContactsActivity : AppCompatActivity() {
    private lateinit var viewBinding : ContactsActivityBinding
    private lateinit var admin : AdminSQLiteOpenHelper
    private lateinit var db : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ContactsActivityBinding.inflate(layoutInflater)
        admin = AdminSQLiteOpenHelper(viewBinding.root.context, "Contacts", null, 1)
        db = admin.writableDatabase
        setContentView(viewBinding.root)
    }

    fun getDataBaseAdmin() : AdminSQLiteOpenHelper{
        return admin
    }

    fun getDataBase() : SQLiteDatabase{
        return db
    }
}