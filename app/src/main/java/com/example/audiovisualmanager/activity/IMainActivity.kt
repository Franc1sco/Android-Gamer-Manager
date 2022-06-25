package com.example.audiovisualmanager.activity

interface IMainActivity {
    fun connectionError()
    fun checkSavedSession()
    fun invalidUser()
    fun validUser(userId: Int)
}