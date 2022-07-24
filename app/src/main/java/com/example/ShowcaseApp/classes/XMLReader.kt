package com.example.showcaseApp.classes

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.lifecycleScope
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactListingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLReader {
    companion object{

        fun import(file : File, db: SQLiteDatabase, activity: ContactListingActivity){
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            val root = document.getElementsByTagName("contacts").item(0)
            val list = root.childNodes

            val bitmap = BitmapFactory.decodeResource(
                activity.baseContext.resources,
                R.drawable.male_avatar
            )
            val bp = Bitmap.createScaledBitmap(bitmap, 500, 500, true)

            switchRootElement(list, bp, db)
            file.delete()
        }

        fun switchRootElement(list : NodeList, bp : Bitmap, db: SQLiteDatabase){
            list.forEach {
                if(it.nodeType == Node.ELEMENT_NODE){
                    val element = it as Element
                    if(element.nodeName == "contact"){
                        Contacts.import(switchElement(element.childNodes), bp, db)
                    }
                }
            }
        }

        fun switchElement(list : NodeList) : List<String>{
            val data = mutableListOf<String>()
            list.forEach {
                data.add((it as Element).textContent)
            }
            return data
        }

        fun NodeList.forEach(action: (Node) -> Unit) {
            (0 until this.length)
                .asSequence()
                .map { this.item(it) }
                .forEach { action(it) }
        }

        fun export(db : SQLiteDatabase, activity : ContactListingActivity){
            val cursor = db.rawQuery("SELECT * FROM contacts", null)

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            val implementation = builder.domImplementation
            val document = implementation.createDocument(null, null, null)
            document.xmlVersion = "1.0"
            document.xmlStandalone = true

            val contactos = document.createElement("contacts")
            document.appendChild(contactos)

            while(cursor.moveToNext()) {
                val contacto = document.createElement("contact")
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