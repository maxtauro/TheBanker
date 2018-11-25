package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class PlayerGameNotificationUtil {

    companion object {

        fun confirmNotification(playerGameNotification: PlayerGameNotification) {

            when (playerGameNotification.notificationType) {
                StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION -> confirmBankPaymentIntent(playerGameNotification)
                StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION -> confirmBankCreditTransaction(playerGameNotification)
            }
        }

        private fun confirmBankCreditTransaction(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processBankPayment(playerGameNotification, BankTransactionEnums.CREDIT)
        }

        private fun confirmBankPaymentIntent(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processBankPayment(playerGameNotification, BankTransactionEnums.DEBIT)
        }


    }
}