package com.example.audiovisualmanager.presenter.interfaces

import android.app.Activity

interface IBasePresenter {
    fun attachView(view: Activity)
    fun detachView()
}