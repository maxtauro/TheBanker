package com.maxtauro.monopolywallet

import java.util.*

/**
 * TODO add authoring, date, and desc
 */
data class GameDao(var hostName : String?, var gameId : String = generateRandomId(), var isGameActive : Boolean = false) {

    val PLAYER_STARTING_CASH : Int = 1500
    val MAX_PLAYERS : Int = 8
    val MIN_PLAYERS : Int = 2


    companion object {
        fun generateRandomId(): String {
            return UUID.randomUUID().toString().replace("-","").substring(0,6)
        }
    }

}
