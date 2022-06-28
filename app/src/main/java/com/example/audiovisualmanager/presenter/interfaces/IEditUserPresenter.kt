package com.example.audiovisualmanager.presenter.interfaces

interface IEditUserPresenter: IBasePresenter {
    suspend fun editUser(userId: Int, password: String)
    suspend fun updateUserStatus(userId: Int, private: Boolean)
}