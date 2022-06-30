package com.example.audiovisualmanager.database

object DatabaseConstants {
    //Declaramos el nombre de la base de datos
    const val DATABASE_NAME = "AudiovisualManagerDatabase"
    
    //Declaramos los nombre de las tablas
    const val TABLE_USER = "UserTable"
    const val TABLE_USERGAMES = "UserGames"
    const val TABLE_GAMES = "Games"
    const val TABLE_FOLLOWERS = "Followers"

    //Declaramos los valores de las columnas
    //TABLA UserTable
    const val ID = "id"
    const val NICK_USER = "nick"
    const val PASSWORD_USER = "password"
    const val PRIVATE_USER = "private"

    //TABLA UserGames intermediaria
    const val GAMEID = "gameid"
    const val GAMEVALIDATED = "validated"

    //TABLA GAMES
    const val GAME_NAME = "name"
    const val GAME_PLATFORM = "platform"
    const val GAME_GENRE = "genre"
    const val GAME_COMPANY = "company"
    const val GAME_POINTS = "valoration"
    const val GAME_STATUS = "status"
    const val GAME_USERID = "userid"
    const val GAME_IMAGE = "image"

    // Tabla seguidores
    const val FOLLOWER_ID = "userid"
    const val FOLLOWED_BY_ID = "followedbyid"
}