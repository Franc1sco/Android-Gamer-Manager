package com.example.audiovisualmanager.presenter.interfaces

interface IMainPresenter: IBasePresenter {
    suspend fun getConnection()
    suspend fun isValidUser(user: String, password: String)
}