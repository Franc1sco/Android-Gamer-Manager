package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.view.interfaces.IMainActivity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.presenter.interfaces.IMainPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainPresenter : IMainPresenter {
    private var view: IMainActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun attachView(view: Activity) {
        this.view = view as IMainActivity
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun getConnection() {
        if (!dbHandler.getConnection()) {
            withContext(Dispatchers.Main) {
                view?.connectionError()
            }
        }
    }

    override suspend fun isValidUser(user: String, password: String) {
        val isValidUser = dbHandler.isValidUser(user, password)
        if (isValidUser == null) {
            withContext(Dispatchers.Main) {
                view?.connectionError()
            }
            return
        }
        if (isValidUser) {
            val userId = dbHandler.getUserId(user)
            withContext(Dispatchers.Main) {
                if (userId == null) {
                    view?.connectionError()
                    return@withContext
                }
                view?.checkSavedSession()
                view?.validUser(userId)
            }
        } else {
            withContext(Dispatchers.Main) {
                view?.invalidUser()
            }
        }
    }
}