package com.example.showcaseApp

import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //Fuerza modo Vertical para está actividad

        val admin = AdminSQLiteOpenHelper(this, "Contacts", null, 1)
        val db : SQLiteDatabase = admin.writableDatabase

        supportFragmentManager.beginTransaction().add(R.id.ac2_fragment, ContactListFragment(db, this)).commit()

        //Close connections and Activity
        findViewById<ImageButton>(R.id.caf_btn_volver).setOnClickListener {
            db.close()
            admin.close()
            finish()
        }
    }
}