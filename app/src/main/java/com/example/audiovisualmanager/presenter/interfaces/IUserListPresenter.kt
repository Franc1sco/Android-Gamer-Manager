package com.example.audiovisualmanager.presenter.interfaces


interface IUserListPresenter: IBasePresenter {
    suspend fun getUserList(userId: Int)

}