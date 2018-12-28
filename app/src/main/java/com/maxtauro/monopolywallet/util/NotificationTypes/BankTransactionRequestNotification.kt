package com.maxtauro.monopolywallet.util.NotificationTypes

/**
 * Notification for bank Transactions
 */

class BankTransactionRequestNotification(gameId : String, playerId: String, playerName: String, paymentAmount: Int, notificationType: StandardNotifications):
        PlayerGameNotification("", playerId, playerName, paymentAmount, gameId, notificationType) {

    enum class MessageDataFields(val dataFieldId: String) {
        PAYMENT_AMOUNT("paymentAmount")
    }
}