package com.example.showcaseApp.classes

import android.database.sqlite.SQLiteDatabase
import com.example.showcaseApp.activities.ContactListingActivity
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLReader {
    companion object{

        fun import(){

        }

        fun export(db : SQLiteDatabase, activity : ContactListingActivity){
            val cursor = db.rawQuery("SELECT * FROM contacts", null)

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            val implementation = builder.domImplementation
            val document = implementation.createDocument(null, null, null)
            document.xmlVersion = "1.0"
            document.xmlStandalone = true

            val contactos = document.createElement("contactos")
            document.appendChild(contactos)

            while(cursor.moveToNext()) {
                val contacto = document.createElement("contacto")
                contactos.appendChild(contacto)

                val name = document.createElement("name")
                name.textContent = cursor.getString(1)
                contacto.appendChild(name)

                val tel = document.createElement("tel")
                tel.textContent = cursor.getString(2)
                contacto.appendChild(tel)

                val info = document.createElement("info")
                info.textContent = cursor.getString(3)
                contacto.appendChild(info)

                File("${activity.getExternalFilesDir(null)}/xml/").mkdirs()
                //Creamos la ruta concreta, con un nombre único y la extensión jpg
                val file = File("${activity.getExternalFilesDir(null)}/xml/export.xml")

                val source = DOMSource(document)
                val result = StreamResult(file)

                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.transform(source, result)
            }
        }
    }
}