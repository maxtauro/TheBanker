package com.maxtauro.monopolywallet.util.NotificationRequestClasses

import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class PaymentRequest(val gameId: String,
                     val playerId: String,
                     val amount: Int,
                     val notificationType: StandardNotifications = StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION) {
}