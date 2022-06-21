package com.example.showcaseApp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int ) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase){ //Crea la tabla pacientes con sus columnas
        db.execSQL("create table pacientes(codigo INTEGER primary key autoincrement, nombre text, especie text, descripcion text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){

    }
}