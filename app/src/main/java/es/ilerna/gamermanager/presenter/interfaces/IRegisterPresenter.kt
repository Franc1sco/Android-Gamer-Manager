package es.ilerna.gamermanager.presenter.interfaces

import es.ilerna.gamermanager.model.User

interface IRegisterPresenter: IBasePresenter {
    suspend fun checkUserExists(name: String)
    suspend fun addUser(user: User)
}