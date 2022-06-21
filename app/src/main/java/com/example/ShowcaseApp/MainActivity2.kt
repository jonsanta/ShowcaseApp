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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //Fuerza modo Vertical para está actividad

        //Creamos el objeto de la clase que gestionara la base de datos
        val admin = AdminSQLiteOpenHelper(this, "PacientesApp", null, 1)
        val bd : SQLiteDatabase = admin.writableDatabase //Variable que permitirá modificar la base de datos

        findViewById<Button>(R.id.btn_insertar).setOnClickListener { // Si pulsamos el botón insertar
            val datos = getText() //Guarda los datos introducidos en el formulario dentro de un Array
            if(datos[0].isEmpty() || datos[1].isEmpty() || datos[2].isEmpty()) {
                //Si algún campo del formulario esta vacio, mandara un Toast pidiendo que se complete
                Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Consulta.insertar(bd, datos) //Insertamos los datos pasados por el formulario mediante la clase Consulta
                Toast.makeText(this, "Datos introducidos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_consultar).setOnClickListener {
            //Si el usuario pulsa el botón consultar se creara un AlertDialog con forma de formulario mediante la clase MiDialog
            //Parámetros: contexto, objeto SQLiteDatabase, título(para el AlertDialog), array de datos(Será null en el caso de Consultar y Eliminar), modo(r: consulta, w: modificar, d: eliminar)
            MiDialog.getFormularioDialog(this, bd, "Introduce los datos del animal sobre el que realizar la consulta", null,"r")
        }

        findViewById<Button>(R.id.btn_modificar).setOnClickListener {
            //Guarda los datos introducidos en el formulario dentro de un Array
            //los pasaremos como parametro datos en MiDialog.getFormularioDialog(contexto, bd, titulo, {{datos}}, modo)
            val datos = getText()

            if(datos[0].isEmpty() || datos[1].isEmpty() || datos[2].isEmpty()) {
                //Si algún campo del formulario esta vacio, mandara un Toast pidiendo que se complete
                Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }
            else //Obtendremos un nuevo formulario para escoger el registro que queremos modificar
            {
                //Parámetros: contexto, objeto SQLiteDatabase, título(para el AlertDialog), array de datos(Será null en el caso de Consultar y Eliminar), modo(r: consulta, w: modificar, d: eliminar)
                MiDialog.getFormularioDialog(this, bd, "Introduce los datos del animal que quieres modificar", datos, "w")
            }
        }

        //Obtendremos un nuevo formulario para escoger el registro que queremos eliminar
        findViewById<Button>(R.id.btn_eliminar).setOnClickListener {
            //Parámetros: contexto, objeto SQLiteDatabase, título(para el AlertDialog), array de datos(Será null en el caso de Consultar y Eliminar), modo(r: consulta, w: modificar, d: eliminar)
            MiDialog.getFormularioDialog(this, bd, "Introduce los datos del animal que quieres eliminar", null,"d")
        }

        findViewById<Button>(R.id.btn_volver2).setOnClickListener {
            bd.close() //Cerramos la base de datos
            admin.close() //Cerramos el admin
            finish()//Cerramos la ventana y volvemos al MainActivity
        }

    }

    //Funcion que devolvera un Array<String> con los datos de los editText de la actividad
    private fun getText() : Array<String>
    {
        val datos = arrayOf(//Guarda los textos de los editText en el array
            findViewById<EditText>(R.id.edit_nombre).text.toString(),
            findViewById<EditText>(R.id.edit_especie).text.toString(),
            findViewById<EditText>(R.id.edit_descripcion).text.toString()
        )

        //Vacia los editText
        findViewById<EditText>(R.id.edit_nombre).setText("")
        findViewById<EditText>(R.id.edit_especie).setText("")
        findViewById<EditText>(R.id.edit_descripcion).setText("")

        return datos //devuelve el array
    }
}
