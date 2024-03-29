package es.ilerna.gamermanager.presenter

import android.app.Activity
import es.ilerna.gamermanager.database.MysqlManager
import es.ilerna.gamermanager.model.Game
import es.ilerna.gamermanager.presenter.interfaces.IMainListPresenter
import es.ilerna.gamermanager.helper.Constants
import es.ilerna.gamermanager.view.interfaces.IMainListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MainListPresenter : IMainListPresenter {
    private var view: IMainListActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    // metodo que se llama cuando se crea el presenter y se le pasa la vista
    override fun attachView(view: Activity) {
        this.view = view as IMainListActivity
    }

    // metodo que se llama cuando se destruye el presenter y se limpia la vista
    override fun detachView() {
        this.view = null
    }

    // metodo para hacer la logica que aplica el filtro de busqueda de la lista de juegos
    override fun applyStatusFilter(filter: Int, listDataFullAdapter: ArrayList<Game>) {
        when (filter) {
            Constants.ITEM_TODOS -> {
                // si el filtro es todos, se muestra todos los juegos
                view?.applyFilterOnView(listDataFullAdapter)
            }
            Constants.ITEM_PROCESO -> {
                // si el filtro es proceso, se muestra solo los juegos en proceso
                val listDataFullAdapterFiltered = ArrayList<Game>()
                for (i in listDataFullAdapter.indices) {
                    if (listDataFullAdapter[i].status == Constants.EN_PROCESO) {
                        listDataFullAdapterFiltered.add(listDataFullAdapter[i])
                    }
                }
                view?.applyFilterOnView(listDataFullAdapterFiltered)
            }
            Constants.ITEM_PENDIENTE -> {
                // si el filtro es pendiente, se muestra solo los juegos pendientes
                val listDataFullAdapterFiltered = ArrayList<Game>()
                for (i in listDataFullAdapter.indices) {
                    if (listDataFullAdapter[i].status == Constants.PENDIENTE) {
                        listDataFullAdapterFiltered.add(listDataFullAdapter[i])
                    }
                }
                view?.applyFilterOnView(listDataFullAdapterFiltered)
            }
            Constants.ITEM_FINALIZADO -> {
                // si el filtro es finalizado, se muestra solo los juegos finalizados
                val listDataFullAdapterFiltered = ArrayList<Game>()
                for (i in listDataFullAdapter.indices) {
                    if (listDataFullAdapter[i].status == Constants.FINALIZADO) {
                        listDataFullAdapterFiltered.add(listDataFullAdapter[i])
                    }
                }
                view?.applyFilterOnView(listDataFullAdapterFiltered)
            }
        }
    }

    // metodo para ordenar la lista de juegos haciendo uso de la base de datos mediante una consulta de ordenamiento
    override suspend fun orderList(userId: Int, orderBy: Int, ascOrder: Boolean) {
        var gamesList: ArrayList<Game>? = null
        when (orderBy) {
            Constants.ITEM_NOMBRE, 0 -> {
                // si el ordenamiento es por nombre, se obtiene la lista de juegos ordenada por nombre
                gamesList = dbHandler.getGamesByUser(userId, "name", ascOrder)
            }
            Constants.ITEM_PLATAFORMA -> {
                // si el ordenamiento es por plataforma, se obtiene la lista de juegos ordenada por plataforma
                gamesList = dbHandler.getGamesByUser(userId, "platform", ascOrder)
            }
            Constants.ITEM_COMPANY -> {
                // si el ordenamiento es por compañia, se obtiene la lista de juegos ordenada por compañia
                gamesList = dbHandler.getGamesByUser(userId, "company", ascOrder)
            }
            Constants.ITEM_GENERO -> {
                // si el ordenamiento es por genero, se obtiene la lista de juegos ordenada por genero
                gamesList = dbHandler.getGamesByUser(userId, "genre", ascOrder)
            }
            Constants.ITEM_VALORACION -> {
                // si el ordenamiento es por valoracion, se obtiene la lista de juegos ordenada por valoracion
                gamesList = dbHandler.getGamesByUser(userId, "valoration", ascOrder)
            }
        }
        // volviendo al hilo principal se muestra el mensaje que corresponda
        withContext(Dispatchers.Main) {
            if (gamesList == null) {
                // si hay error al obtener la lista de juegos, se muestra un mensaje de error
                view?.connectionError()
                return@withContext
            }
            // se muestra la lista de juegos ordenada en la vista
            view?.applyOrderOnView(gamesList)
        }
    }

    // metodo para borrar un juego de la base de datos mediante una consulta de borrado
    override suspend fun removeGame(gameId: Int) {
        dbHandler.deleteGame(gameId)
    }

    // Metodo para que un usuario deje de seguir a otro usuario
    override suspend fun unfollowBy(userId: Int, viewerId: Int) {
        val noError = dbHandler.unfollowBy(userId, viewerId)
        withContext(Dispatchers.Main) {
            if (noError.not()) {
                view?.connectionError()
                return@withContext
            }
            // se muestra la lista de juegos ordenada
            view?.followStatusChanged(false)
        }
    }

    // Metodo para agregar que un usuario siga a otro usuario
    override suspend fun followBy(userId: Int, viewerId: Int) {
        val noError = dbHandler.followBy(userId, viewerId)
        withContext(Dispatchers.Main) {
            if (noError.not()) {
                view?.connectionError()
                return@withContext
            }
            view?.followStatusChanged(true)
        }
    }
}