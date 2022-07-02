package es.ilerna.gamermanager.view.interfaces

import es.ilerna.gamermanager.model.User
import java.util.ArrayList

interface IUserListActivity {
    fun connectionError()
    fun loadUserList(userList: ArrayList<User>)
}