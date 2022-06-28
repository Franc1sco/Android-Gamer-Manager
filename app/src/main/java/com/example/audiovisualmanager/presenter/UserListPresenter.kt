package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.presenter.interfaces.IUserListPresenter
import com.example.audiovisualmanager.view.interfaces.IUserListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserListPresenter: IUserListPresenter {
    private var view: IUserListActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    // metodo que se llama cuando se crea el presenter y se asocia con la vista
    override fun attachView(view: Activity) {
        this.view = view as IUserListActivity
    }

    // metodo que se llama cuando se destruye el presenter y se desasocia con la vista
    override fun detachView() {
        this.view = null
    }

    // metodo que se llama cuando se carga la vista y se obtiene la lista de usuarios de la base de datos
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