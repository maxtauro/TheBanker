package com.maxtauro.monopolywallet.util

import android.content.Intent
import android.support.annotation.WorkerThread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.GameDao
import com.maxtauro.monopolywallet.HostGame
import com.maxtauro.monopolywallet.JoinGame
import com.maxtauro.monopolywallet.service.NotificationService
import com.maxtauro.monopolywallet.util.NotificationTypes.BankTransactionRequestNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification

/**
 * A helper class for using firebase realtime database.
 * This should be the only place you interact with the Db from here unless ABSOLUTELY necessary
 */

class FirebaseHelper(val gameId: String) {

    //Utils
    var notificationUtil = FirebaseNotificationUtil(gameId)
    private var firebaseReferenceUtil: FirebaseReferenceUtil = FirebaseReferenceUtil(gameId)

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

    fun leaveGame(playerName: String) {
        playerListRef.child(playerName).removeValue()
    }


    @WorkerThread
    fun createPayBankIntentRequest(paymentTransactionRequest: BankTransactionRequestNotification, creditDebit: BankTransactionEnums) {

        val hostId = getGameHostUid(gameId)
        val notificationRef = firebaseReferenceUtil.getPlayerNotificationRef(hostId).push()

        notificationRef.setValue(paymentTransactionRequest)

        val requestKey: String = notificationRef.key!!
        notificationRef.child(FirebaseReferenceConstants.PLAYER_NOTIFICATION_KEY_KEY).setValue(requestKey)
    }

    fun processBankPayment(paymentNotification: PlayerGameNotification, creditDebit: BankTransactionEnums) {

        val playerId = paymentNotification.playerId
        val notificationKey = paymentNotification.notificationKey
        val amount = paymentNotification.amount

        val playerBalanceRef = firebaseReferenceUtil.getPlayerBalanceRef(playerId)

        playerBalanceRef.runTransaction(object : Transaction.Handler {
            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                removePlayerNotification(notificationKey, playerId)
            }

            override fun doTransaction(balance: MutableData): Transaction.Result {

                if (balance.value == null) {
                    TODO("IMPLEMENT ERROR HANDLING")
                    return Transaction.abort()
                }

                var newBalance: Long = if (creditDebit == BankTransactionEnums.DEBIT) {
                    (balance.value as Long) - amount
                }
                else (balance.value as Long) + amount

                balance.value = newBalance
                return Transaction.success(balance)
            }

        })

    }

    private fun removePlayerNotification(notificationKey: String, playerId: String) {

        val notificationRef = firebaseReferenceUtil.getPlayerNotificationRef(playerId).child(notificationKey)

        notificationRef.removeValue()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        TODO("IMPLEMENT ERROR HANDLING")
                    }
                }
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