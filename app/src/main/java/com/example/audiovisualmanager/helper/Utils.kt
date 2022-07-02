package com.example.audiovisualmanager.helper

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
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

    // Evita que el usuario pueda cambiar a tema oscuro en la aplicación
    fun disallowDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}