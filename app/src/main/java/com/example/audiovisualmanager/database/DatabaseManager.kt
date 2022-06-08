package com.example.audiovisualmanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.audiovisualmanager.model.User

class DatabaseManager(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        //Declaramos el nombre de la base de datos y su version
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "AudiovisualManagerDatabase"

        //Declaramos los nombre de las tablas
        private const val TABLE_USER = "UserTable"

        //Declaramos los valores de las columnas
        //TABLA UserTable
        private const val NICK_USER = "nick"
        private const val PASSWORD_USER = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        initDB(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        initDB(db)
    }

    private fun initDB(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + NICK_USER + " TEXT,"
                + PASSWORD_USER + " TEXT" + ")")
        db?.execSQL(query)
    }

    fun addUser(user: User) {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NICK_USER, user.name)
        contentValues.put(PASSWORD_USER, user.password)

        db.insert(TABLE_USER, null, contentValues)

        db.close()
    }

    fun isValidUser(username: String, password: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        val bd = this.writableDatabase
        val dbResponse = bd.rawQuery("select $NICK_USER from $TABLE_USER where $NICK_USER = '$username' and $PASSWORD_USER = '$password'", null)

        return dbResponse.count > 0.also {
            bd.close()
            dbResponse.close()
        }
    }
}