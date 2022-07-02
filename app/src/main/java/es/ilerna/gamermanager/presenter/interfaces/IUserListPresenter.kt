package es.ilerna.gamermanager.presenter.interfaces


interface IUserListPresenter: IBasePresenter {
    suspend fun getUserList(userId: Int)

}