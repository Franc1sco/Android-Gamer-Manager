package com.example.audiovisualmanager.data

import com.google.gson.annotations.SerializedName

data class GameModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("released")
    val released: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("updated")
    val updated: String
)
