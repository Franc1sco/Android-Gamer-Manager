package es.ilerna.gamermanager.presenter.interfaces

import es.ilerna.gamermanager.model.Game

interface IAddGamePresenter: IBasePresenter {
    suspend fun updateGame(game: Game)
    suspend fun addGameByUserid(game: Game, userId: Int)
    suspend fun getGameById(gameId: Int)
}