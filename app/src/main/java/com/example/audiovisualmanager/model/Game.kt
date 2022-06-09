package com.example.audiovisualmanager.model

data class Game(
    val name: String,
    val status: String,
    val platform: String,
    val id: Int,
    val description: String? = null
)
