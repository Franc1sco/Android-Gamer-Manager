package es.ilerna.gamermanager.presenter

import android.app.Activity
import es.ilerna.gamermanager.database.MysqlManager
import es.ilerna.gamermanager.model.Game
import es.ilerna.gamermanager.presenter.interfaces.IAddGamePresenter
import es.ilerna.gamermanager.view.interfaces.IAddGameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddGamePresenter: IAddGamePresenter {
    private var view: IAddGameActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    // metodo que se llama cuando se crea la vista de la actividad para adjuntarle el presenter
    override fun attachView(view: Activity) {
        this.view = view as IAddGameActivity
    }

    // metodo que se llama cuando se destruye la vista de la actividad para liberar el presenter
    override fun detachView() {
        this.view = null
    }

    // metodo que se llama para actualizar el juego en la base de datos y se actualiza la vista
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

    // metodo para agregar un juego a la base de datos y se actualiza la vista
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

    // metodo para obtener la informacion del juego de la base de datos y se actualiza la vista con la informacion
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