package com.maxtauro.monopolywallet.util.NotificationTypes

/**
 * Notification for bank Transactions
 */

class BankTransactionRequestNotification(gameId : String, playerId: String, paymentAmount: Int, notificationType: StandardNotifications):
        PlayerGameNotification("", playerId, paymentAmount, gameId, notificationType) {

    enum class MessageDataFields(val dataFieldId: String) {
        PAYMENT_AMOUNT("paymentAmount")
    }
}