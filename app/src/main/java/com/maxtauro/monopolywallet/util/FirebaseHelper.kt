package com.maxtauro.monopolywallet.util

import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.maxtauro.monopolywallet.Player


class FirebaseHelper(val gameId: String) {

    var database = FirebaseDatabase.getInstance()
    var databaseRef = database.reference

    var gameRef: DatabaseReference
    var playerListRef: DatabaseReference
    var hostRef: DatabaseReference

    init {
        gameRef = databaseRef.child(gameId)
        playerListRef = gameRef.child("playerList")
        hostRef = gameRef.child("host")
    }

    fun createGame(gameId : String, hostName : String) {

        gameRef.child("host").setValue(hostName) //TODO have child be player or host object

        var player = Player(gameId, hostName)
        playerListRef.child(player.playerName).setValue(player)
    }

    fun joinGame(gameId : String, playerName : String) {

        if (gameIdExists(gameId)) {
            var player = Player(gameId, playerName)
            playerListRef.child(player.playerName).setValue(player)
        }
    }

    fun gameIdExists(gameId : String): Boolean {

        var gameIdExists = true
        var gameRef = databaseRef.child(gameId)

        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                gameIdExists = dataSnapshot!!.exists()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return gameIdExists
    }

    fun deleteGame() {
        databaseRef.child(gameId).removeValue()
    }

    fun leaveGame(playerName: String) {
        playerListRef.child(playerName).removeValue()
    }


}