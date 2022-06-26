package com.example.audiovisualmanager.presenter

import android.app.Activity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.presenter.interfaces.IEditUserPresenter
import com.example.audiovisualmanager.view.interfaces.IEditUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditUserPresenter: IEditUserPresenter {
    private var view: IEditUserActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun attachView(view: Activity) {
        this.view = view as IEditUserActivity
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun editUser(userId: Int, password: String, private: Boolean) {
        val noError = dbHandler.updateUser(userId, password, private)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.updateUserSuccess()
            } else {
                view?.connectionError()
            }
        }
    }
}