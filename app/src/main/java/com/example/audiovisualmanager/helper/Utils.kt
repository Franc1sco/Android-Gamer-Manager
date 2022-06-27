package com.example.audiovisualmanager.helper

import android.content.Context
import android.widget.Toast
import com.example.audiovisualmanager.R

object Utils {
    // Muestra un mensaje toast
    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Muestra un mensaje toast con una cadena de texto personalizada de error
    fun connectionError(context: Context) {
        showMessage(context, context.getString(R.string.connection_error))
    }
}