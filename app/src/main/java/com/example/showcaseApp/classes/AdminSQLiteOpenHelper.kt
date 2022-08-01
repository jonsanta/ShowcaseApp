package com.example.showcaseApp.classes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int ) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase){
        db.execSQL("create table contacts(id INTEGER primary key autoincrement, name TEXT, number INTEGER, info TEXT, icon BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        //Never used. Implementation required for AdminSQLiteOpenHelper
    }
}