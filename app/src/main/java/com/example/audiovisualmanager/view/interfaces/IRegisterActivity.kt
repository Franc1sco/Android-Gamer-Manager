package com.example.audiovisualmanager.view.interfaces

interface IRegisterActivity {
    fun connectionError()
    fun showUserExists()
    fun showUserDoesNotExist()
    fun userAddedSuccessfully()
}