package com.example.audiovisualmanager.view.interfaces

import com.example.audiovisualmanager.model.Game

interface IAddGameActivity {
    fun connectionError()
    fun updateOrAddGameSuccess()
    fun loadGame(game: Game)
}