package com.example.audiovisualmanager.helper

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.database.MysqlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Utils {
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    // Muestra un mensaje toast
    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Muestra un mensaje toast con una cadena de texto personalizada de error
    suspend fun connectionError(context: Context) {
        val reconnect = dbHandler.tryReconnect()
        withContext(Dispatchers.Main) {
            if (!reconnect) {
                showMessage(context, context.getString(R.string.connection_error))
            } else {
                showMessage(context, context.getString(R.string.connection_reconnect))
            }
        }
    }

    fun disallowDarkMode(context: Context) {
        context.resources.configuration.uiMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()
    }
}