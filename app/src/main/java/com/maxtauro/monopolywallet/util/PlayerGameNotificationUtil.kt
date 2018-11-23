package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.util.NotificationTypes.PayBankIntentNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class PlayerGameNotificationUtil {

    companion object {

        fun confirmNotification(playerGameNotifications: PlayerGameNotification) {

            when (playerGameNotifications.NOTIFICATION_TYPE) {
                StandardNotifications.PAY_BANK_INTENT_NOTIFICATION -> confirmBankPaymentIntent(playerGameNotifications)
            }
        }

        private fun confirmBankPaymentIntent(playerGameNotifications: PlayerGameNotification) {

            val firebaseHelper = FirebaseHelper(playerGameNotifications.gameId)
            firebaseHelper.processBankPayment(playerGameNotifications as PayBankIntentNotification, BankTransactionEnums.DEBIT)
        }


    }
}