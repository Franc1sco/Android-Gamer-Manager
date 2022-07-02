package es.ilerna.gamermanager.view.interfaces

interface IRegisterActivity {
    fun connectionError()
    fun showUserExists()
    fun showUserDoesNotExist()
    fun userAddedSuccessfully()
}