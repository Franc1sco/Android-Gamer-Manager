package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.model.User
import com.example.audiovisualmanager.presenter.interfaces.IRegisterPresenter
import com.example.audiovisualmanager.view.interfaces.IRegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterPresenter: IRegisterPresenter {
    private var view: IRegisterActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    override fun attachView(view: Activity) {
        this.view = view as IRegisterActivity
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun checkUserExists(name: String) {
        val userExists = dbHandler.checkUserExists(name)
        withContext(Dispatchers.Main) {
            when (userExists) {
                null -> {
                    view?.connectionError()
                }
                true -> {
                    view?.showUserExists()
                }
                else -> {
                    view?.showUserDoesNotExist()
                }
            }
        }
    }

    override suspend fun addUser(user: User) {
        val noError = dbHandler.addUser(user)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.userAddedSuccessfully()
            } else {
                view?.connectionError()
            }
        }
    }
}