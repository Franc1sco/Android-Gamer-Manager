package com.example.audiovisualmanager

class DatabaseHelper {
    fun isValidUser(username: String, password: String): Boolean {
        return username == "admin" && password == "admin"
    }
}