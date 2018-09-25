package com.maxtauro.monopolywallet

class  Player {

    var playerName: String
    var playerId: String
    var playerBalance: Int = 1500

    constructor(playerName : String, playerId : String, gameBank: GameBank) {
        this.playerName = playerName
        this.playerId = playerId
    }

    fun getMoney(debitAmount : Int) {
        playerBalance += debitAmount
    }

    fun payBank(creditAmount: Int) {
        playerBalance -= creditAmount
    }

}