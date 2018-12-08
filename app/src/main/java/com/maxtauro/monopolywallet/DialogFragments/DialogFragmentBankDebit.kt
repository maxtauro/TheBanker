package com.maxtauro.monopolywallet.DialogFragments


import com.maxtauro.monopolywallet.Constants.BankTransactionEnums
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil

/**
 * Dialog Fragment for sending a payment to the Bank
 */
class DialogFragmentBankDebit: DialogFragmentPaymentBase() {

    override fun onPositiveButtonClick(paymentAmount: Int) {
        val notificationUtil = FirebaseNotificationUtil(gameId)
        notificationUtil.sendBankTransactionNotification(paymentAmount, auth.uid, BankTransactionEnums.DEBIT)
    }

    companion object {
        private const val TAG = "Fragment Bank Debit"
        private const val CLICKED_PAY = "User clicked Pay button"
    }

}