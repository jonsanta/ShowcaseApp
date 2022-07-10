package com.example.showcaseApp

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.showcaseApp.databinding.ActivityMain3Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity3 : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMain3Binding
    private val CAMERA_REQUEST_CODE = 123
    private lateinit var nombreUnico : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //Fuerza modo Vertical para está actividad

        findViewById<Button>(R.id.abrir).setOnClickListener{
            if(checkPermissions()) abrirCamara()//Si los permisos están concedidos
            else
            {
                //Pedimos los permisos de cámara, escritura y lectura
                val permisos = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                //Lanzamos la petición pasando el array de strings con los permisos y la constante con el código
                requestPermissions(permisos, CAMERA_REQUEST_CODE)
            }
        }

        findViewById<Button>(R.id.btn_volver3).setOnClickListener{
            finish() //Cerramos la ventana y volvemos al Main Activity
        }
    }

    private fun abrirCamara(){
        viewBinding = ActivityMain3Binding.inflate(layoutInflater) //Se utilizará para mostrar la cámara

        //Creamos el directorio PacImagenes dentro de la estructura de la aplicación
        File("${getExternalFilesDir(null)}/PacImagenes/").mkdirs()

        //Creamos la ruta concreta, con un nombre único y la extensión jpg
        val file = File("${getExternalFilesDir(null)}/PacImagenes/"+getNombreUnico())

        //Utilizando fileProvider generamos un URI correspondiente a la estructura de datos de nuestra aplicación
        val photoURI = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        //Creamos el Uri donde se guardaran las imágenes
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //Intent que lanzara la aplicación de cámara predeterminada del dispositivo
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI) //Añadimos un URI donde guardar las fotos al intent
        camara.launch(intent) //Lanzamos la cámara
    }

    private fun getNombreUnico() : String
    {//Genera un String único a partir de la fecha-hora-minutos-segundos actuales
        nombreUnico = "imagen_"+SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Date())+".jpg"
        return nombreUnico
    }

    private fun checkPermissions() : Boolean //Devuelve true si los permisos están concedidos, de lo contrario false
    {
        return (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            abrirCamara() //La aplicación dispone de los permisos del dispositivo, se llama a la función que abrirá la cámara
        else
            //Los permisos no han sido concedidos
            Toast.makeText(this, "No se han concedido los permisos", Toast.LENGTH_SHORT).show()
    }

    //Solución actual que reemplaza el antiguo OnActivityResult() que esta deprecated
    private val camara = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK) { //La actividad se ha ejecutado satisfactoriamente
            //Cuando capturamos una foto, esta se guarda en el URI mencionado anteriormente y es eliminada del Intent.data
            //provocando que el bitmap mostrado posteriormente a la captura sea null y lanzando un error crítico
            //SOLUCIÓN: Cuando la Imagen es capturada capturamos la excepcion NullPointerException
                try{
                    val bitmap = it.data!!.extras!!.get("data") as Bitmap //Se guardan los datos recibidos por la cámara en un bitmap
                    viewBinding.camara.setImageBitmap(bitmap) //Se muestra el bitmap por medio del viewBinding
                }catch (e: NullPointerException)
                {//En caso de recibir NullPointerException mostraremos la imagen que se guardo
                    val file = File("${getExternalFilesDir(null)}/PacImagenes/"+nombreUnico)
                    viewBinding.camara.setImageURI(file.toUri())

                    //ADDS captured Image to the gallery bitmap lists
                    lifecycleScope.launch{
                        withContext(Dispatchers.IO){MainActivity4.setBitmap(file)}
                    }
                }
        }
    }
}