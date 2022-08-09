package com.example.showcaseApp.classes

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.showcaseApp.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Utils {
    companion object{
        /**
         * Copy given InputStream into given File
         * @param input : InputStream that contains input file
         * @param copy : Contains output file
         * @return output file
         */
        fun copyFile(input : InputStream?, copy: File) : File {
            FileOutputStream(copy).use { out ->
                val buf = ByteArray(1024)
                var len: Int
                while (input?.read(buf).also { len = it!! }!! > 0) {
                    out.write(buf, 0, len)
                }
            }
            input?.close()
            return copy
        }

        //Close software keyboard
        fun closeKeyboard(context: Context?, view : View?){
            val imm = (context?.getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
        
        fun getURLOfDrawable(resId : Int) : String{
            return Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/" +resId).toString()
        }

        fun preventTwoClick(view : View){
            view.isEnabled = false
            view.postDelayed(
                { view.isEnabled = true },
                500
            )
        }
    }
}