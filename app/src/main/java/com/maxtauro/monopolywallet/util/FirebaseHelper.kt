package com.maxtauro.monopolywallet.util

import android.content.Intent
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


/**
 * TODO add authoring, date, and desc
 */

class FirebaseHelper(val gameId: String) {

    //Utils
    var notificationUtil = FirebaseNotificationUtil(gameId)
    private lateinit var firebaseReferenceUtil: FirebaseReferenceUtil

    //References
    val databaseReference = FirebaseDatabase.getInstance().reference
    var gameRef: DatabaseReference
    var hostNotificationListRef: DatabaseReference
    var playerListRef: DatabaseReference
    var hostRef: DatabaseReference

    //Auth
    var auth = FirebaseAuth.getInstance()

    init {
        firebaseReferenceUtil = FirebaseReferenceUtil(gameId)
        gameRef = firebaseReferenceUtil.databaseRef.child(gameId)
        hostNotificationListRef = gameRef.child(FirebaseReferenceConstants.HOST_NOTIFICATION_LIST_KEY)
        playerListRef = gameRef.child(FirebaseReferenceConstants.PLAYER_LIST_NODE_KEY)
        hostRef = gameRef.child(firebaseReferenceUtil.getHostPath())
    }

    //TODO rename this function (or refactor so it makes more sense
    fun createGame(hostName: String) {

        var game = GameDao(auth.uid, gameId)

        gameRef.child("gameInfo").setValue(game)
    }

    fun joinGame(gameId : String, playerName : String) {

        if (gameIdExists(gameId)) {
            val player = Player(auth.uid!!, playerName) //TODO deal w/ non-null assertion better
            playerListRef.child(auth.uid!!).setValue(player)
        }
    }

    fun gameIdExists(gameId : String): Boolean {

        var gameIdExists = true
        var gameRef = firebaseReferenceUtil.databaseRef.child(gameId)

        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                gameIdExists = dataSnapshot!!.exists()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return gameIdExists
    }

    fun startGame() {
        val gameActivePath = firebaseReferenceUtil.getGameActivePath()
        databaseReference.child(gameActivePath).setValue(true)
    }

    fun deleteGame() {
        firebaseReferenceUtil.databaseRef.child(gameId).removeValue()
    }

    fun leaveGame(playerName: String) {
        playerListRef.child(playerName).removeValue()
    }

    fun createPaymentRequest(paymentRequest: HashMap<String, Any>) {
        hostNotificationListRef.push().setValue(paymentRequest)
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