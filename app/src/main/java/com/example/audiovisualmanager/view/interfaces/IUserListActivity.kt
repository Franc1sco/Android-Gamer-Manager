package com.example.audiovisualmanager.view.interfaces

import com.example.audiovisualmanager.model.User
import java.util.ArrayList

interface IUserListActivity {
    fun connectionError()
    fun loadUserList(userList: ArrayList<User>)
}