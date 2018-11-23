package com.maxtauro.monopolywallet.util

import com.google.firebase.database.FirebaseDatabase
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

    fun sendBankPaymentNotification(paymentAmount: Int, playerId: String?) {

        if (playerId == null) {
            throw NullPointerException("Could not get player ID") //TODO implement an error handling framework
        }

        val notificationBuilder = NotificationBuilder()
        notificationBuilder.setGameAndNotificationType(gameId, StandardNotifications.PAY_BANK_INTENT_NOTIFICATION)

        notificationBuilder.addItem("amount", paymentAmount)
        notificationBuilder.addItem("playerId", playerId)

        val notification = notificationBuilder.build()

        sendNotification(notification)

        val firebaseHelper = FirebaseHelper(gameId)
        firebaseHelper.createPaymentRequest(notification)
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
            notification["NOTIFICATION_TYPE"] = notificationType.toString()
        }


    }

}