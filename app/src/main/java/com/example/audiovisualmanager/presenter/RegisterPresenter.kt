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

    // metodo para asociar el presenter con la vista
    override fun attachView(view: Activity) {
        this.view = view as IRegisterActivity
    }

    // metodo para desasociar el presenter con la vista y liberar recursos
    override fun detachView() {
        this.view = null
    }

    // metodo para validar los datos del usuario
    override suspend fun checkUserExists(name: String) {
        val userExists = dbHandler.checkUserExists(name)
        withContext(Dispatchers.Main) {
            when (userExists) {
                null -> {
                    // si hay error de conexion con la base de datos
                    view?.connectionError()
                }
                true -> {
                    // si el usuario ya existe
                    view?.showUserExists()
                }
                else -> {
                    // si el usuario no existe
                    view?.showUserDoesNotExist()
                }
            }
        }
    }

    // metodo para registrar un usuario
    override suspend fun addUser(user: User) {
        val noError = dbHandler.addUser(user)
        withContext(Dispatchers.Main) {
            if (noError) {
                // si no hay error al registrar el usuario
                view?.userAddedSuccessfully()
            } else {
                view?.connectionError()
            }
        }
    }
}