package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.Constants.BankTransactionEnums
import com.maxtauro.monopolywallet.Constants.PlayerTransactionEnum
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

/**
 * Util class for processing player notifications
 */
class PlayerGameNotificationUtil {

    companion object {

        fun confirmNotification(playerGameNotification: PlayerGameNotification) {

            when (playerGameNotification.notificationType) {
                StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION -> confirmBankPaymentIntent(playerGameNotification)
                StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION -> confirmBankCreditTransaction(playerGameNotification)
                StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST -> confirmPlayerRequestTransaction(playerGameNotification)
                StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST -> confirmPlayerSendTransaction(playerGameNotification)
            }
        }

        fun declineNotification(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.declineNotification(playerGameNotification)
        }

        private fun confirmBankCreditTransaction(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processBankPayment(playerGameNotification, BankTransactionEnums.CREDIT)
        }

        private fun confirmBankPaymentIntent(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processBankPayment(playerGameNotification, BankTransactionEnums.DEBIT)
        }

        private fun confirmPlayerSendTransaction(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processPlayerTransaction(playerGameNotification, PlayerTransactionEnum.SEND_MONEY)

        }

        private fun confirmPlayerRequestTransaction(playerGameNotification: PlayerGameNotification) {
            val firebaseHelper = FirebaseHelper(playerGameNotification.gameId)
            firebaseHelper.processPlayerTransaction(playerGameNotification, PlayerTransactionEnum.REQUEST_MONEY)

        }

    }
}