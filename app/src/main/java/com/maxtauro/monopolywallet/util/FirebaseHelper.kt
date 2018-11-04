package com.maxtauro.monopolywallet.util

import android.content.Intent
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.maxtauro.monopolywallet.Player
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.maxtauro.monopolywallet.GameDao
import com.maxtauro.monopolywallet.HostGame
import com.maxtauro.monopolywallet.JoinGame
import java.util.concurrent.ExecutionException


/**
 * TODO add authoring, date, and desc
 */

class FirebaseHelper(val gameId: String) {

    //Notification Utils
    var notificationUtil = FirebaseNotificationUtil()

    // Database
    var database = FirebaseDatabase.getInstance()
    var databaseRef = database.reference

    //References
    var gameRef: DatabaseReference
    var playerListRef: DatabaseReference
    var hostRef: DatabaseReference

    //Auth
    var auth = FirebaseAuth.getInstance()

    init {
        gameRef = databaseRef.child(gameId)
        playerListRef = gameRef.child("playerList")
        hostRef = gameRef.child("host")
    }

    //TODO rename this function (or refactor so it makes more sense
    fun createGame(hostName: String) {

        var game = GameDao(auth.uid, gameId)

        gameRef.child("gameInfo").setValue(game) //TODO have child be player or host object

        var player = Player(auth.uid!!, hostName)
        playerListRef.child(player.playerName).setValue(player)
    }

    fun joinGame(gameId : String, playerName : String) {

        if (gameIdExists(gameId)) {
            var player = Player(auth.uid!!, playerName)
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

    fun startGame() {
        gameRef.child("gameInfo/gameActive").setValue(true) //TODO put the path string in some enum
    }

    fun deleteGame() {
        databaseRef.child(gameId).removeValue()
    }

    fun leaveGame(playerName: String) {
        playerListRef.child(playerName).removeValue()
    }

    companion object {

        fun getGameHostUid(gameId: String): String {

            val database = FirebaseDatabase.getInstance()
            val taskHelper = TaskHelper()
            val gameHostRef = database.getReference("$gameId/gameInfo/hostName")

            return taskHelper.getValueSynchronously(gameHostRef)
        }

        @Deprecated("We start the game Sychronously now, use this as reference for Async methods")
        fun startGameAsync(gameId: String, uid: String?, notificationService: NotificationService) {
            val database = FirebaseDatabase.getInstance()
            val gameHostRef = database.getReference("$gameId/gameInfo/hostName")


            gameHostRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val gameHostId = dataSnapshot.value

                    if (gameHostId == uid) {
                        val startGameIntent = Intent(notificationService, HostGame::class.java)
                        startGameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        notificationService.startActivity(startGameIntent)
                    }

                    else {
                        val startGameIntent = Intent(notificationService, JoinGame::class.java)
                        startGameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        notificationService.startActivity(startGameIntent)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    }

}