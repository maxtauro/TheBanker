package com.maxtauro.monopolywallet.Firebase

import android.content.Intent
import android.support.annotation.WorkerThread
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.GameDao
import com.maxtauro.monopolywallet.Activities.HostGameActivity
import com.maxtauro.monopolywallet.Activities.NonHostGameActivity
import com.maxtauro.monopolywallet.Constants.FirebaseReferenceConstants
import com.maxtauro.monopolywallet.service.NotificationService
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil
import com.maxtauro.monopolywallet.util.FirebaseReferenceUtil
import com.maxtauro.monopolywallet.util.TaskHelper
import com.google.firebase.auth.UserProfileChangeRequest



/**
 * A helper class for using firebase realtime database.
 * This should be the only place you interact with the Db from here unless ABSOLUTELY necessary
 */

open class FirebaseHelper(val gameId: String) {

    //Utils
    var notificationUtil = FirebaseNotificationUtil(gameId)
    protected var firebaseReferenceUtil: FirebaseReferenceUtil = FirebaseReferenceUtil(gameId)

    //References
    val databaseReference = FirebaseDatabase.getInstance().reference
    var gameRef: DatabaseReference
    var playerListRef: DatabaseReference
    var hostRef: DatabaseReference

    //Auth
    var auth = FirebaseAuth.getInstance()

    init {
        gameRef = firebaseReferenceUtil.databaseRef.child(gameId)
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

    fun leaveGame(playerId: String) {
        playerListRef.child(playerId).removeValue()
    }

    //TODO change this from a pair, its to confusing
    protected fun removePlayerNotification(notificationInfo: Pair<String, String>) {

        val notificationRef = firebaseReferenceUtil
                .getPlayerNotificationRef(notificationInfo.first)
                .child(notificationInfo.second)

        notificationRef.removeValue()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        TODO("IMPLEMENT ERROR HANDLING")
                    }
                }
    }

    fun setPlayerIsActive(playerId: String, isActive: Boolean) {
        playerListRef.child(playerId).child(FirebaseReferenceConstants.PLAYER_ACTIVE_NODE_KEY).setValue(isActive)
    }

    fun moveGameToCompletedNode() {
        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val completedGamesRef = firebaseReferenceUtil.databaseRef.child(FirebaseReferenceConstants.COMPLETED_GAMES_REF)
                completedGamesRef.child(gameId).setValue(gameRef)
                gameRef.removeValue()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    fun setDisplayName(hostName: String) {
        val currentUser = auth.currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(hostName).build()

        currentUser?.updateProfile(profileUpdates)
    }

    companion object {

        private const val TAG = "FirebaseHelper"

        @WorkerThread
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
                        val startGameIntent = Intent(notificationService, HostGameActivity::class.java)
                        startGameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        notificationService.startActivity(startGameIntent)
                    }

                    else {
                        val startGameIntent = Intent(notificationService, NonHostGameActivity::class.java)
                        startGameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        notificationService.startActivity(startGameIntent)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    }

}