package com.maxtauro.monopolywallet

/**
 * DAO Class for players
 */
class Player {

    var playerName: String
    var playerId: String
    var playerBalance: Int = 1500
    var playerActive = true

    @Deprecated("Need a no argument ctor for firebase, you should NEVER call this explicitly")
    constructor() {
        playerActive = false // If a malformed player entry is created, it will be hidden
        playerName = "didn't init"
        playerId = "id didn't init"
    }

    constructor(playerId : String, playerName : String) {
        this.playerId = playerId
        this.playerName = playerName
    }

}