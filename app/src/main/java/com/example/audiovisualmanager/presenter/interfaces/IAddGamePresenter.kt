package com.example.audiovisualmanager.presenter.interfaces

import com.example.audiovisualmanager.model.Game

interface IAddGamePresenter: IBasePresenter {
    suspend fun updateGame(game: Game)
    suspend fun addGameByUserid(game: Game, userId: Int)
    suspend fun getGameById(gameId: Int)
}