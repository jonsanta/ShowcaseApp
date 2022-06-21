package com.example.showcaseApp

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

//CLASE CONSULTA - GESTIONARA LOS SELECT-UPDATE-DELETE DE LA BASE DE DATOS
class Consulta {
    companion object {
        data class linea(val id: Int, val texto: String) //Data Class que guardara el ID del registro encontrado y el texto que se mostrara el lista
        /*
        La bd, el ID único del registro(si no se pasa un ID su valor será 0 por defecto) y el nombre
        @Return: Devuelve una Lista de lineas(Data Class comentada mas arriba) - Tambien puede devolver Null
        */
       fun consultar(bd: SQLiteDatabase, id: Int = 0, nombre: String) : List<linea>? {
            if(id > 0) {//Si se ha recibido un ID, se realizara un SELECT buscando con el ID
                //Dicho SELECT se guardará en un Cursor
                val cursor = bd.rawQuery("SELECT * FROM pacientes WHERE codigo =" + id, null)
                return mostrarInformacion(cursor) //Retornamos la lista obtenida en mostrarInformacion()
            } else if(nombre != "") {//Si no se ha recibido un ID, se realizara un SELECT buscando con el nombre
                //Dicho SELECT se guardará en un Cursor
                val cursor = bd.rawQuery("SELECT * FROM pacientes WHERE UPPER(nombre) ='" + nombre.uppercase() + "'", null)
                return mostrarInformacion(cursor) //Retornamos la lista obtenida en mostrarInformacion()
            }else return null //En caso de no encontrar ningún registro en la consulta, se devolverá null
        }
        /*
        Recibe por parametro el cursor que guarda los datos recibidos de la consulta
        @Return: Devuelve una Lista de lineas(Data Class comentada mas arriba) - Tambien puede devolver Null
        */
        private fun mostrarInformacion(cursor: Cursor) : List<linea>?
        {
            val lista : MutableList<linea> = mutableListOf() //lista de lineas(Data Class comentada mar arriba)

            if(cursor.count == 0) return null //Si la consulta no ha encontrado ningún registro coincidente, devuelve null

            val cabeceras = arrayOf("ID", "Nombre", "Especie", "Desripción") //Nombres de columna de la tabla

            while(cursor.moveToNext())//Mientras haya información en el cursor
            {
                var text = ""//Variable que guardara un String con cada una de las columnas de la tabla separadas con \n
                for(x in 0..3)//Iteramos el cursor para obtener cada columna y añadir su contenido al String antes mencionado
                {
                    if(x == 0) {
                        text += cabeceras[x]+": "+cursor.getString(x)
                    }
                    else text += "\n"+cabeceras[x]+": "+cursor.getString(x)
                }

                lista.add(linea(cursor.getString(0).toInt(), text))//Añadimos A lista una linea(ID, texto)
            }
            cursor.close()//Cerramos el cursor

            return lista//Devolvemos la lista que contiene los ID y textos de cada una de las lineas devueltas por el cursor
        }

        //Ejecuta la sentencia INSERT con los datos introducidos
        fun insertar(bd: SQLiteDatabase, datos : Array<String>)
        {
            val registro = ContentValues()
            registro.put("nombre", datos[0])//la columna nombre se rellenara con datos[0]
            registro.put("especie", datos[1])//la columna especie se rellenara con datos[1]
            registro.put("descripcion", datos[2])//la columna descripción se rellenara con datos[2]
            bd.insert("pacientes", null, registro)
            registro.clear()
        }

        //Ejecuta la sentencia UPDATE para el registro que coincida con el ID pasado por parámetro
        fun modificar(bd : SQLiteDatabase, datos : Array<String>, id : Int)
        {
            val registro = ContentValues()
            registro.put("nombre", datos[0])//la columna nombre se rellenara con datos[0]
            registro.put("especie", datos[1])//la columna especie se rellenara con datos[1]
            registro.put("descripcion", datos[2])//la columna descripción se rellenara con datos[2]
            bd.update("pacientes", registro, "codigo='" + id + "'", null)
            registro.clear()
        }

        //Ejecuta la sentencia DELETE para el registro que coincida con el ID pasado por parámetro
        fun eliminar(bd: SQLiteDatabase, id: Int){
            bd.delete("pacientes", "codigo='" + id + "'", null)
        }


    }
}