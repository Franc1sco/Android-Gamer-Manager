package com.example.audiovisualmanager.presenter.interfaces

import com.example.audiovisualmanager.model.Game
import java.util.ArrayList

interface IMainListPresenter: IBasePresenter {
    fun applyStatusFilter(filter: Int, listDataFullAdapter: ArrayList<Game>)
    suspend fun orderList(userId: Int, orderBy: Int, ascOrder: Boolean)
    suspend fun removeGame(gameId: Int)
}