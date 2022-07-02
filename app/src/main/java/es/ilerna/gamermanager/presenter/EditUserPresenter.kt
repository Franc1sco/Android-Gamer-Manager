package es.ilerna.gamermanager.presenter

import android.app.Activity
import es.ilerna.gamermanager.database.MysqlManager
import es.ilerna.gamermanager.presenter.interfaces.IEditUserPresenter
import es.ilerna.gamermanager.view.interfaces.IEditUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditUserPresenter: IEditUserPresenter {
    private var view: IEditUserActivity? = null
    private var dbHandler: MysqlManager = MysqlManager().getInstance()

    // metodo que se ejecuta cuando se crea el presenter y se le pasa la vista
    override fun attachView(view: Activity) {
        this.view = view as IEditUserActivity
    }

    // metodo que se ejecuta cuando se destruye el presenter y se limpia la vista
    override fun detachView() {
        this.view = null
    }

    // metodo que se ejecuta cuando se quiere editar un usuario y se mandan los datos a la base de datos para actualizarlo
    override suspend fun editUser(userId: Int, password: String) {
        val noError = dbHandler.updateUser(userId, password)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.updateUserSuccess()
            } else {
                view?.connectionError()
            }
        }
    }

    // metodo que se ejecuta cuando se quiere editar la privacidad de un usuario y se mandan los datos a la base de datos para actualizarlo
    override suspend fun updateUserStatus(userId: Int, private: Boolean) {
        val noError = dbHandler.updateUserStatus(userId, private)
        withContext(Dispatchers.Main) {
            if (noError) {
                view?.updateUserStatusSuccess()
            } else {
                view?.connectionError()
            }
        }
    }
}