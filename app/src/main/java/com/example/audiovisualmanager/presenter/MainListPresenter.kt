package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.presenter.interfaces.IMainListPresenter
import com.example.audiovisualmanager.helper.Constants
import com.example.audiovisualmanager.view.interfaces.IMainListActivity
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
                gamesList = dbHandler.getGamesPendingByUserid(userId, "name", ascOrder)
            }
            Constants.ITEM_PLATAFORMA -> {
                // si el ordenamiento es por plataforma, se obtiene la lista de juegos ordenada por plataforma
                gamesList = dbHandler.getGamesPendingByUserid(userId, "platform", ascOrder)
            }
            Constants.ITEM_COMPANY -> {
                // si el ordenamiento es por compañia, se obtiene la lista de juegos ordenada por compañia
                gamesList = dbHandler.getGamesPendingByUserid(userId, "company", ascOrder)
            }
            Constants.ITEM_GENERO -> {
                // si el ordenamiento es por genero, se obtiene la lista de juegos ordenada por genero
                gamesList = dbHandler.getGamesPendingByUserid(userId, "genre", ascOrder)
            }
            Constants.ITEM_VALORACION -> {
                // si el ordenamiento es por valoracion, se obtiene la lista de juegos ordenada por valoracion
                gamesList = dbHandler.getGamesPendingByUserid(userId, "valoration", ascOrder)
            }
        }
        withContext(Dispatchers.Main) {
            if (gamesList == null) {
                view?.connectionError()
                return@withContext
            }
            // se muestra la lista de juegos ordenada
            view?.applyOrderOnView(gamesList)
        }
    }

    // metodo para borrar un juego de la base de datos mediante una consulta de borrado
    override suspend fun removeGame(gameId: Int) {
        dbHandler.deleteGame(gameId)
    }
}