package com.maxtauro.monopolywallet.util.NotificationTypes

/**
 * Notification for P2p transactions
 **/
class PlayerTransactionRequestNotification(val recipientId: String, gameId : String, playerId: String, paymentAmount: Int, notificationType: StandardNotifications):
        PlayerGameNotification("", playerId, paymentAmount, gameId, notificationType) {

    enum class MessageDataFields(val dataFieldId: String) {
        RECIPIENT_ID("recipientId")
    }
}