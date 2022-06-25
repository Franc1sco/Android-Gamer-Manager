package com.example.audiovisualmanager.utils

import android.content.Context
import android.widget.Toast
import com.example.audiovisualmanager.R

object Utils {
    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun connectionError(context: Context) {
        showMessage(context, context.getString(R.string.connection_error))
    }
}