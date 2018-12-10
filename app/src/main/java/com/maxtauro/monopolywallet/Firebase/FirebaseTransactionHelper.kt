package com.maxtauro.monopolywallet.Firebase

import android.support.annotation.WorkerThread
import com.google.firebase.database.*
import com.maxtauro.monopolywallet.Constants.BankTransactionEnums
import com.maxtauro.monopolywallet.Constants.FirebaseReferenceConstants
import com.maxtauro.monopolywallet.Constants.PlayerTransactionEnum
import com.maxtauro.monopolywallet.util.NotificationTypes.BankTransactionRequestNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerTransactionRequestNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class FirebaseTransactionHelper(gameId: String) : FirebaseHelper(gameId) {

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
        val notificationInfo = Pair(auth.uid!!, notificationKey)

        processTransaction(playerBalanceRef, amount, creditDebit, notificationInfo)
    }

    //TODO I should make the following two methods cleaner, there's no need to have both methods if they're this similar
    fun createSendMoneyRequest(paymentAmount: Int, senderId: String?, recipientId: String) {
        val notificationRef = firebaseReferenceUtil.getPlayerNotificationRef(recipientId).push()
        val playerTransactionRequestNotification = PlayerTransactionRequestNotification(auth.uid!!, gameId, senderId!!, paymentAmount, StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST)

        notificationRef.setValue(playerTransactionRequestNotification)
        val requestKey: String = notificationRef.key!!
        notificationRef.child(FirebaseReferenceConstants.PLAYER_NOTIFICATION_KEY_KEY).setValue(requestKey)
    }

    fun createRequestMoneyRequest(paymentAmount: Int, senderId: String?, recipientId: String) {
        val notificationRef = firebaseReferenceUtil.getPlayerNotificationRef(recipientId).push()
        val playerTransactionRequestNotification = PlayerTransactionRequestNotification(auth.uid!!, gameId, senderId!!, paymentAmount, StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST)

        notificationRef.setValue(playerTransactionRequestNotification)
        val requestKey: String = notificationRef.key!!
        notificationRef.child(FirebaseReferenceConstants.PLAYER_NOTIFICATION_KEY_KEY).setValue(requestKey)
    }

    fun processPlayerTransaction(playerGameNotification: PlayerGameNotification, tranType: PlayerTransactionEnum) {
        val playerId = playerGameNotification.playerId
        val notificationKey = playerGameNotification.notificationKey
        val amount = playerGameNotification.amount

        val senderBalanceRef = firebaseReferenceUtil.getPlayerBalanceRef(playerId)
        val currUserBalanceRef = firebaseReferenceUtil.getPlayerBalanceRef(auth.uid)

        val senderCreditDebit =
                if (tranType == PlayerTransactionEnum.REQUEST_MONEY) BankTransactionEnums.CREDIT
                else BankTransactionEnums.DEBIT

        val currUserCreditDebit =
                if (tranType == PlayerTransactionEnum.REQUEST_MONEY) BankTransactionEnums.DEBIT
                else BankTransactionEnums.CREDIT

        val notificationInfo = Pair(auth.uid!!, notificationKey)

        processTransaction(senderBalanceRef, amount, senderCreditDebit)
        processTransaction(currUserBalanceRef, amount, currUserCreditDebit, notificationInfo)
    }

    fun declineNotification(playerGameNotification: PlayerGameNotification) {
        val notificationKey = playerGameNotification.notificationKey
        val notificationInfo = Pair(auth.uid!!, notificationKey)
        removePlayerNotification(notificationInfo)
        //TODO send declination notification
    }

    private fun processTransaction(balanceRef: DatabaseReference, amount: Int, creditDebit: BankTransactionEnums, notificationInfo: Pair<String, String>? = null) {
        balanceRef.runTransaction(object : Transaction.Handler {
            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if (notificationInfo != null) {
                    removePlayerNotification(notificationInfo)
                }
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
}