package com.example.audiovisualmanager.model

data class User(
    val name: String,
    val password: String,
    val isprivate: Int = 0,
    val userid: Int = 0
)
