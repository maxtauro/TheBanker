package com.maxtauro.monopolywallet.util

import com.google.firebase.database.FirebaseDatabase
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerTransactionRequestNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications
import java.lang.NullPointerException

/**
 * Util Class for sending Firebase Notifications
 */
class FirebaseNotificationUtil(val gameId: String) {

    //TODO put create some interface to hold notification Fields

    private val ref = FirebaseDatabase.getInstance().reference
    private val notifications = ref.child("notificationRequests")

    fun startGame() {
        val notificationBuilder = NotificationBuilder()
        notificationBuilder.setGameAndNotificationType(gameId, StandardNotifications.START_GAME_NOTIFICATION)
        sendNotification(notificationBuilder.build())
    }

    fun sendBankTransactionNotification(paymentAmount: Int, playerId: String?, creditDebit: BankTransactionEnums) {

        if (playerId == null) {
            TODO("Implement Error handling framework")
        }

        val notificationBuilder = NotificationBuilder()

        val notificationType = if (creditDebit == BankTransactionEnums.DEBIT) {
            StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION
        }
        else {
            StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION
        }

        notificationBuilder.setGameAndNotificationType(gameId, notificationType)

        notificationBuilder.addItem("paymentAmount", paymentAmount)
        notificationBuilder.addItem("playerId", playerId)

        val notification = notificationBuilder.build()

        sendNotification(notification)

    }

    fun sendSendMoneyNotification(paymentAmount: Int, playerId: String?, recipientId: String) {

        if (playerId == null) {
            TODO("Implement Error handling framework")
        }

        val notificationBuilder = NotificationBuilder()
        notificationBuilder.setGameAndNotificationType(gameId, StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST)

        notificationBuilder.addItem("paymentAmount", paymentAmount)
        notificationBuilder.addItem("playerId", playerId)
        notificationBuilder.addItem(PlayerTransactionRequestNotification.MessageDataFields.RECIPIENT_ID.toString(), recipientId)

        val notification = notificationBuilder.build()
        sendNotification(notification)

    }

    fun sendRequestMoneyNotification(paymentAmount: Int, playerId: String?, recipientId: String) {

        if (playerId == null) {
            TODO("Implement Error handling framework")
        }

        val notificationBuilder = NotificationBuilder()
        notificationBuilder.setGameAndNotificationType(gameId, StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST)

        notificationBuilder.addItem("paymentAmount", paymentAmount)
        notificationBuilder.addItem("playerId", playerId)
        notificationBuilder.addItem(PlayerTransactionRequestNotification.MessageDataFields.RECIPIENT_ID.toString(), recipientId)

        val notification = notificationBuilder.build()
        sendNotification(notification)
    }

    private fun sendNotification(notification: HashMap<String, Any>) {
        notifications.push().setValue(notification)
    }

    private class NotificationBuilder {

        private val notification = HashMap<String, Any>()

        fun addItem(key: String, value: Any) {
            notification[key] = value
        }

        fun build(): HashMap<String, Any> {
            return notification
        }

        //TODO, should I set these in the constructor?
        fun setGameAndNotificationType(gameId: String, notificationType: StandardNotifications) {
            notification["gameId"] = gameId //TODO, put these keys in some enum
            notification["notificationType"] = notificationType.toString()
        }


    }

}