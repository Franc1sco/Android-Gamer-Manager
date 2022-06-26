package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.presenter.interfaces.IUserListPresenter
import com.example.audiovisualmanager.utils.Utils
import com.example.audiovisualmanager.view.interfaces.IUserListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserListPresenter: IUserListPresenter {
    private var view: IUserListActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    override fun attachView(view: Activity) {
        this.view = view as IUserListActivity
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun getUserList(userId: Int) {
        val userList = dbHandler.getUserList(userId)
        withContext(Dispatchers.Main) {
            if (userList == null) {
                view?.connectionError()
                return@withContext
            }
            view?.loadUserList(userList)
        }
    }
}