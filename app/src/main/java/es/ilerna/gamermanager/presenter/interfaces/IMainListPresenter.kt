package es.ilerna.gamermanager.presenter.interfaces

import es.ilerna.gamermanager.model.Game
import java.util.ArrayList

interface IMainListPresenter: IBasePresenter {
    fun applyStatusFilter(filter: Int, listDataFullAdapter: ArrayList<Game>)
    suspend fun orderList(userId: Int, orderBy: Int, ascOrder: Boolean)
    suspend fun removeGame(gameId: Int)
    suspend fun unfollowBy(userId: Int, viewerId: Int)
    suspend fun followBy(userId: Int, viewerId: Int)
}