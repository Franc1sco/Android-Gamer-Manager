package es.ilerna.gamermanager.view.interfaces

interface IMainActivity {
    fun connectionError()
    fun checkSavedSession()
    fun invalidUser()
    fun validUser(userId: Int)
}