package com.example.audiovisualmanager.presenter.interfaces

import com.example.audiovisualmanager.model.User

interface IRegisterPresenter: IBasePresenter {
    suspend fun checkUserExists(name: String)
    suspend fun addUser(user: User)
}