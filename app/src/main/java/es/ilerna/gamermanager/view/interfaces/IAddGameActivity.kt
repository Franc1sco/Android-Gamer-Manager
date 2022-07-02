package es.ilerna.gamermanager.view.interfaces

import es.ilerna.gamermanager.model.Game

interface IAddGameActivity {
    fun connectionError()
    fun updateOrAddGameSuccess()
    fun loadGame(game: Game)
}