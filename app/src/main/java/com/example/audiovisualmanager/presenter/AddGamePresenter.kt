package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.presenter.interfaces.IAddGamePresenter
import com.example.audiovisualmanager.view.interfaces.IAddGameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddGamePresenter: IAddGamePresenter {
    private var view: IAddGameActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    override fun attachView(view: Activity) {
        this.view = view as IAddGameActivity
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun updateGame(game: Game) {
        val noError = dbHandler.updateGame(game)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.updateOrAddGameSuccess()
            } else {
                view?.connectionError()
            }
        }
    }

    override suspend fun addGameByUserid(game: Game, userId: Int) {
        val noError = dbHandler.addGameByUserid(game, userId)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.updateOrAddGameSuccess()
            } else {
                view?.connectionError()
            }
        }
    }

    override suspend fun getGameById(gameId: Int) {
        val game = dbHandler.getGameById(gameId)
        withContext(Dispatchers.Main) {
            if (game != null) {
                view?.loadGame(game)
            } else {
                view?.connectionError()
            }
        }
    }
}