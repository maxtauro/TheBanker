package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.util.NotificationTypes.HostNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class HostNotificationUtil {

    companion object {

        fun confirmNotification(hostNotification: HostNotification) {

            when (hostNotification.NOTIFICATION_TYPE) {
                StandardNotifications.PAY_BANK_INTENT_NOTIFICATION -> confirmBankPaymentIntent(hostNotification)
            }
        }

        private fun confirmBankPaymentIntent(hostNotification: HostNotification) {

            val firebaseHelper = FirebaseHelper(hostNotification.gameId)

            firebaseHelper.processBankPayment(hostNotification, BankTransactionEnums.DEBIT)
        }


    }
}