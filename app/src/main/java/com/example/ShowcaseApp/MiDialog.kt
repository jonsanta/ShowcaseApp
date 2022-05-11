package com.example.ShowcaseApp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.widget.*

class MiDialog {
    companion object {
        private lateinit var alertDialog : AlertDialog //AlertDialog Global
        /*
        Recibe por parámetro el contexto, la bd, el ID unico, el titulo para el AlertDialog,
        los datos introducidos en el Formulario del activity y el modo
        */
        fun getFormularioDialog(context: Context, bd : SQLiteDatabase, titulo: String, datos: Array<String>?, mode : String){
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)//Genera un AlertDialog.Builder
            builder.setTitle(titulo)//Asigna el título al builder creado

            val editId = EditText(context) //EditText que se usara para buscar registros por ID
            editId.inputType = InputType.TYPE_CLASS_NUMBER//Asignamos un tipo numérico al EditText
            editId.hint = "Busca por ID"

            val editNombre = EditText(context)//EditText que se usara para buscar registros por Nombre
            editNombre.inputType = InputType.TYPE_CLASS_TEXT//Asignamos un tipo texto al EditText
            editNombre.hint = "Busca por Nombre"


            val linear = getLinear(context)//Generamos un Linear layout parametrizado
            linear.addView(editId)//Añadimos los EditText como hijos del LinearLayout
            linear.addView(editNombre)
            builder.setView(linear)//Añadimos el LinearLayout con los EditText a la vista del builder

            alertDialog = builder.create()//Generamos el AlertDialog a partir del builder
            //El botón Cerrara el AlertDialog y llamara al método buscar con los datos recogidos en los EditText
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Consultar"){ dialog, which ->
                alertDialog.dismiss()
                if(editId.text.toString() == "")//Si el EditText del ID esta vacio, pasaremos el ID -1 para evitar errores de conversión
                    buscar(context, bd, -1, editNombre.text.toString(), datos, mode)
                else//De lo contrario pasaremos el ID(String) convertido a número
                    buscar(context, bd, Integer.parseInt(editId.text.toString()), editNombre.text.toString(), datos, mode)
            }
            //El botón cerrara el AlertDialog y anulara el proceso de búsqueda
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancelar"){ dialog, which -> alertDialog.dismiss() }
            alertDialog.show()//Mostramos en pantalla el AlertDialog que contiene toda la interfaz generada
        }
        /*
        Recibe por parámetro el contexto, la bd, el ID unico, el nombre buscado,
        los datos introducidos en el Formulario del activity y el modo
        */
        private fun buscar(context: Context, bd: SQLiteDatabase, id: Int, nombre: String, datos: Array<String>?, mode: String){
            var titulo : String = ""
            when (mode) {//Dependiendo del modo se rea
                "r" -> titulo = "Datos encontrados"
                "w" -> titulo = "Pulsa el registro que quieres modificar"
                "d" -> titulo = "Pulsa el registro que quieres eliminar"
            }
            //Obtenemos el AlertDialog con la información buscada
            getConsultaDialog(context, bd, titulo,Consulta.consultar(bd, id, nombre), datos, mode)
        }
        /*
        Recibe por parámetro el contexto,la bd, el título que mostrara el AlertDialog,
        la lista que contiene cada uno de los registros y los datos obtenidos en formulario del activity
        */
        private fun getConsultaDialog(context: Context, bd: SQLiteDatabase, titulo : String, lista: List<Consulta.Companion.linea>?,datos: Array<String>?, mode: String)
        {
            //Si se recibe un null significa que no se encontraron registros coincidentes durante la consulta
            if(lista == null) Toast.makeText(context, "No existe ese regitro en la base de datos", Toast.LENGTH_SHORT).show()
            else//Si se recibe una lista con registros
            {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context) //Genera un AlertDialog.Builder
                builder.setTitle(titulo)//Asigna el título al builder creado

                //Creamos un ScrollView para el caso en el que se reciban muchos registros a mostrar
                val scrollView = ScrollView(context)

                val linear = getLinear(context) //Generamos un Linear layout parametrizado
                linear.orientation = LinearLayout.VERTICAL //Asignamos la orientación del LinearLayout a vertical
                for(item in lista)
                {
                    //Por cada ítem existente en la lista creamos un botón que se rellenara con la información de cada uno de los registros
                    val id = item.id
                    val btn = getButton(context, bd, id, datos, mode)
                    btn.setText(item.texto)
                    linear.addView(btn) //Este bóton es añadido al LinearLayout recientemente generado
                }

                scrollView.addView(linear)//Añadimos el LinearLayout que contiene toda la estructura creada como hijo del ScrollView
                builder.setView(scrollView)//Añadimos el ScrollView que a su vez contiene el Linear Layout a la vista del builder

                alertDialog = builder.create()//Generamos el AlertDialog a partir del builder
                //El botón cerrar cerrara el AlertDialog
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cerrar"){ dialog, which -> alertDialog.dismiss() }
                alertDialog.show()//Mostramos en pantalla el AlertDialog que contiene toda la interfaz generada
            }
        }

        fun getLinear(context: Context) : LinearLayout
        {
            val linear = LinearLayout(context)//Genera un LinearLayout
            linear.orientation = LinearLayout.VERTICAL//Orientación vertical
            val params = LinearLayout.LayoutParams( //Parámetros de tamaño
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            linear.layoutParams = params //Asigna los parámetros al LinearLayout

            return linear //Devuelve el LinearLayout
        }
        /*
        Recibe por parámetro el contexto, la bd, el ID único del registro,
        los datos introducidos por formulario de Activity y el modo(r: consulta, w: modificar, d: eliminar)
        @Return: Devuelve un Boton
        */
        fun getButton(context: Context, bd : SQLiteDatabase, id: Int, datos: Array<String>?, mode : String) : Button
        {
            val btn = Button(context)//Crea un botón vacío
            val params = LinearLayout.LayoutParams( //Parámetros de tamaño
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            btn.layoutParams = params //Asigna los parámetros al boton
            btn.gravity = Gravity.CENTER //Alinea el texto al centro del botón
            //Por defecto, el texto de los botones se transforma automáticamente a mayúsculas
            btn.transformationMethod = null //transformationMethod = null. Desactiva lo antes mencionado

            if(mode == "r") { //Si se generan en el modo Consulta
                btn.setBackgroundColor(Color.TRANSPARENT)//Color de fondo del botón transparente
                btn.isEnabled = false //Desactiva la acción de pulsar del botón
            }

            btn.setOnClickListener { //Genera la funcionalidad al presionar el boton en los distintos modos
                if(mode == "w" && datos != null) {//Si se generan en el modo Modificar
                    Consulta.modificar(bd, datos, id) //Utiliza la clase Consulta para realizar el UPDATE
                    Toast.makeText(context, "Los datos han sido modificados", Toast.LENGTH_SHORT).show()
                }
                else if(mode == "d"){//Si se generan en el modo Eliminar
                    Consulta.eliminar(bd, id)//Utiliza la clase Consulta para realizar el DELETE
                    Toast.makeText(context, "Los datos han sido eliminados", Toast.LENGTH_SHORT).show()
                }
                alertDialog.dismiss()//Una vez realizada la acción del botón, cerramos el AlertDialog
            }

            return btn //Devuelve el botón generado
        }
    }
}