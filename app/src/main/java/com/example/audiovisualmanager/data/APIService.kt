package com.example.audiovisualmanager.data

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("/api/games?key=c72138147fc64a98b169e87879f2e25b")
    fun getGames(): Response<GameApiResponse<GameModel>>
}