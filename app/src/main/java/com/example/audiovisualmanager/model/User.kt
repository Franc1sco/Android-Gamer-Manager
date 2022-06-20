package com.example.audiovisualmanager.model

data class User(
    val name: String,
    val password: String,
    val private: Int = 0,
    val userid: Int = 0
)
