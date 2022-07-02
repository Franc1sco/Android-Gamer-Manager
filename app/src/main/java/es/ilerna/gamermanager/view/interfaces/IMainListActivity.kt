package es.ilerna.gamermanager.view.interfaces

import es.ilerna.gamermanager.model.Game

interface IMainListActivity {
    fun applyFilterOnView(gameList: ArrayList<Game>)
    fun connectionError()
    fun applyOrderOnView(gameList: ArrayList<Game>)
    fun followStatusChanged(isFollowed: Boolean)
}