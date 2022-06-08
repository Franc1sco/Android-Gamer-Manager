package com.example.audiovisualmanager.data

import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants.FINALIZADO
import com.example.audiovisualmanager.utils.Constants.PC

object RemoteDataMapper {

    fun mapListCharacterRestModelToCharacter(responseList: List<GameModel>?): List<Game>{

        val result = ArrayList<Game>()

        if (responseList==null)
            return result

        for (element in responseList){
            result.add(
                Game(
                    name = element.name,
                    status = FINALIZADO,
                    platform = PC
                ))
        }
        return result
    }
}
