package com.example.showcaseApp.classes

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.ContactsActivity
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
        fun import(file : File, activity: ContactsActivity){
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            val bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                activity.baseContext.resources,
                R.drawable.male_avatar
            ), 500, 500, true)

            switchContact(document.getElementsByTagName("contacts").item(0).childNodes, bitmap, activity.getDataBase())
            file.delete()
        }

        private fun switchContact(list : NodeList, bitmap: Bitmap, db: SQLiteDatabase){
            list.forEach {
                if(it.nodeType == Node.ELEMENT_NODE){
                    val element = it as Element
                    if(element.nodeName == "contact"){
                        Contacts.import(switchContactElement(element.childNodes), bitmap, db)
                    }
                }
            }
        }

        private fun switchContactElement(elements : NodeList) : List<String>{
            val list = mutableListOf<String>()
            elements.forEach {
                list.add((it as Element).textContent)
            }

            return list
        }

        private fun NodeList.forEach(action: (Node) -> Unit) {
            (0 until this.length)
                .asSequence()
                .map { this.item(it) }
                .forEach { action(it) }
        }

        fun export(activity : ContactsActivity){
            val cursor = activity.getDataBase().rawQuery("SELECT * FROM contacts", null)

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            val document = builder.domImplementation.createDocument(null, null, null)
            document.xmlVersion = "1.0"
            document.xmlStandalone = true

            val contacts = document.createElement("contacts")
            document.appendChild(contacts)

            while(cursor.moveToNext()) {
                val contact = document.createElement("contact")
                contacts.appendChild(contact)

                val name = document.createElement("name")
                name.textContent = cursor.getString(1)
                contact.appendChild(name)

                val tel = document.createElement("tel")
                tel.textContent = cursor.getString(2)
                contact.appendChild(tel)

                val info = document.createElement("info")
                info.textContent = cursor.getString(3)
                contact.appendChild(info)

                File("${activity.getExternalFilesDir(null)}/xml/").mkdirs()

                val source = DOMSource(document)
                val result = StreamResult(File("${activity.getExternalFilesDir(null)}/xml/export.xml"))

                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.transform(source, result)
            }
            cursor.close()
        }
    }
}