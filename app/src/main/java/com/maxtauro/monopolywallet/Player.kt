package com.maxtauro.monopolywallet

class  Player {

    var playerName: String
    var playerId: String
    var playerBalance: Int = 1500

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