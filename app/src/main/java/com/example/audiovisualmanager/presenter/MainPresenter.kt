package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.activity.IMainActivity
import com.example.audiovisualmanager.database.MysqlManager

class MainPresenter : IMainPresenter {
    private var view: IMainActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun attachView(view: Activity) {
        this.view = view as IMainActivity
    }

    override fun detachView() {
        this.view = null
    }

    override fun getConnection() {
        dbHandler.getConnection()
    }

    override fun isValidUser(user: String, password: String) {
        val isValidUser = dbHandler.isValidUser(user, password)
        if (isValidUser == null) {
            view?.connectionError()
            return
        }
        if (isValidUser) {
            val userId = dbHandler.getUserId(user)
            if (userId == null) {
                view?.connectionError()
                return
            }
            view?.checkSavedSession()
            view?.validUser(userId)
        } else {
            view?.invalidUser()
        }
    }
}