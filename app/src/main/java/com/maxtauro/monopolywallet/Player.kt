package com.maxtauro.monopolywallet

/**
 * TODO add authoring, date, and desc
 */
class Player {

    var playerName: String
    var playerId: String
    var playerBalance: Int = 1500

    // Need a no argument ctor for firebase, you should NEVER call this explicitly
    constructor() {
        playerName = "didn't init"
        playerId = "id didn't init"
    }

    constructor(playerId : String, playerName : String) {
        this.playerId = playerId
        this.playerName = playerName
    }

    fun getMoney(debitAmount : Int) {
        playerBalance += debitAmount
    }

    fun payBank(creditAmount: Int) {
        playerBalance -= creditAmount
    }

}