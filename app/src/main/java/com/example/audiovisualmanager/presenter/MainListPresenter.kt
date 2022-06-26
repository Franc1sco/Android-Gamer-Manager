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
    override fun attachView(view: Activity) {
        this.view = view as IMainListActivity
    }

    override fun detachView() {
        this.view = null
    }

    override fun applyStatusFilter(filter: Int, listDataFullAdapter: ArrayList<Game>) {
        when (filter) {
            Constants.ITEM_TODOS -> {
                view?.applyFilterOnView(listDataFullAdapter)
            }
            Constants.ITEM_PROCESO -> {
                val listDataFullAdapterFiltered = ArrayList<Game>()
                for (i in listDataFullAdapter.indices) {
                    if (listDataFullAdapter[i].status == Constants.EN_PROCESO) {
                        listDataFullAdapterFiltered.add(listDataFullAdapter[i])
                    }
                }
                view?.applyFilterOnView(listDataFullAdapterFiltered)
            }
            Constants.ITEM_PENDIENTE -> {
                val listDataFullAdapterFiltered = ArrayList<Game>()
                for (i in listDataFullAdapter.indices) {
                    if (listDataFullAdapter[i].status == Constants.PENDIENTE) {
                        listDataFullAdapterFiltered.add(listDataFullAdapter[i])
                    }
                }
                view?.applyFilterOnView(listDataFullAdapterFiltered)
            }
            Constants.ITEM_FINALIZADO -> {
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

    override suspend fun orderList(userId: Int, orderBy: Int) {
        var gamesList: ArrayList<Game>? = null
        when (orderBy) {
            Constants.ITEM_NOMBRE -> {
                gamesList = dbHandler.getGamesPendingByUserid(userId, "name")
            }
            Constants.ITEM_PLATAFORMA -> {
                gamesList = dbHandler.getGamesPendingByUserid(userId, "platform")
            }
            Constants.ITEM_COMPANY -> {
                gamesList = dbHandler.getGamesPendingByUserid(userId, "company")
            }
            Constants.ITEM_GENERO -> {
                gamesList = dbHandler.getGamesPendingByUserid(userId, "genre")
            }
            Constants.ITEM_VALORACION -> {
                gamesList = dbHandler.getGamesPendingByUserid(userId, "valoration")
            }
        }
        withContext(Dispatchers.Main) {
            if (gamesList == null) {
                view?.connectionError()
                return@withContext
            }
            view?.applyOrderOnView(gamesList)
        }
    }

    override suspend fun removeGame(gameId: Int) {
        dbHandler.deleteGame(gameId)
    }
}