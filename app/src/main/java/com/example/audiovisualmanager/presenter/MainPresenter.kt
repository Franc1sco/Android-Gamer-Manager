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

    // metodo que se llama cuando se crea la vista y se asocia con el presenter
    override fun attachView(view: Activity) {
        this.view = view as IMainActivity
    }

    // metodo que se llama cuando se destruye la vista y se libera el presenter
    override fun detachView() {
        this.view = null
    }

    // metodo para hacer la conexion con la base de datos
    override suspend fun getConnection() {
        if (!dbHandler.getConnection()) {
            withContext(Dispatchers.Main) {
                view?.connectionError()
            }
        }
    }

    // metodo para comprobar en base de datos si existe un usuario con el nombre y contraseña ingresados
    override suspend fun isValidUser(user: String, password: String) {
        val isValidUser = dbHandler.isValidUser(user, password)
        if (isValidUser == null) {
            // si hay un error en la base de datos se muestra un mensaje de error
            withContext(Dispatchers.Main) {
                view?.connectionError()
            }
            return
        }
        if (isValidUser) {
            // si el usuario es valido se obtiene el id del usuario
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
            // si el usuario no es valido se muestra un mensaje de error
            withContext(Dispatchers.Main) {
                view?.invalidUser()
            }
        }
    }
}