package com.maxtauro.monopolywallet

import android.provider.ContactsContract
import com.google.firebase.database.*

class FirebaseHelper(gameId: String) {

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
            override fun onDataChange(snapshot: DataSnapshot?) {
                gameIdExists = snapshot!!.exists()
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })

        return gameIdExists
    }

    fun deleteGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun leaveGame() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}