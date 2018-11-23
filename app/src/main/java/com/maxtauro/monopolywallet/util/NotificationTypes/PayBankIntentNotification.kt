package com.maxtauro.monopolywallet.util.NotificationTypes

/**
 * Notification for paying the bank
 */

class PayBankIntentNotification(gameId : String, playerId: String, paymentAmount: Int):
        PlayerGameNotification("", playerId, paymentAmount, gameId, StandardNotifications.PAY_BANK_INTENT_NOTIFICATION) {}