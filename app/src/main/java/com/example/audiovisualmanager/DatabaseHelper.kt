package com.example.audiovisualmanager

class DatabaseHelper {
    fun isValidUser(username: String, password: String): Boolean {
        return username == "jonay" && password == "admin"
    }
}