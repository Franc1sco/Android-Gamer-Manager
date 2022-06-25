package com.example.audiovisualmanager.presenter

import android.app.Activity

interface IMainPresenter {
    fun attachView(view: Activity)
    fun detachView()
    fun getConnection()
    fun isValidUser(user: String, password: String)
}