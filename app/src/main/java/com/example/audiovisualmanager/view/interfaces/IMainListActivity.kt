package com.example.audiovisualmanager.view.interfaces

import com.example.audiovisualmanager.model.Game

interface IMainListActivity {
    fun applyFilterOnView(gameList: ArrayList<Game>)
    fun connectionError()
    fun applyOrderOnView(gameList: ArrayList<Game>)
    fun followStatusChanged(isFollowed: Boolean)
}