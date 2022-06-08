package com.example.audiovisualmanager.data

import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants.PENDIENTE
import com.example.audiovisualmanager.utils.Constants.EN_PROCESO
import com.example.audiovisualmanager.utils.Constants.FINALIZADO
import com.example.audiovisualmanager.utils.Constants.PC
import com.example.audiovisualmanager.utils.Constants.PLAYSTATION
import com.example.audiovisualmanager.utils.Constants.XBOX


class DatabaseHelper {
    fun isValidUser(username: String, password: String): Boolean {
        return username == "admin" && password == "admin"
    }

    fun generateGameList(): List<Game> {
        val gameList = ArrayList<Game>()

        gameList.add(Game("Counter Strike 1.6", PENDIENTE, PC))
        gameList.add(Game("Fifa", PENDIENTE, PLAYSTATION))
        gameList.add(Game("Call of Duty", EN_PROCESO, XBOX))
        gameList.add(Game("The sims 4", FINALIZADO, PLAYSTATION))
        gameList.add(Game("Counter strike source", FINALIZADO, PC))
        gameList.add(Game("Call of Duty", FINALIZADO, XBOX))

        return gameList
    }
}