package es.ilerna.gamermanager.presenter.interfaces

interface IMainPresenter: IBasePresenter {
    suspend fun getConnection()
    suspend fun isValidUser(user: String, password: String)
}