package com.baray.zohor.arduinocontroller.main.fragments.connection.util
import android.content.Context
import android.widget.Toast


class Util(private val context: Context){
         fun showNotification(msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
}