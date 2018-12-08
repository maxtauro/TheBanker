package com.maxtauro.monopolywallet.DialogFragments

import com.maxtauro.monopolywallet.Constants.BankTransactionEnums
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil

/**
 * Dialog to input amount for bank credit transactions
 */

class DialogFragmentBankCredit: DialogFragmentPaymentBase() {

    override fun onPositiveButtonClick(paymentAmount: Int) {
        val notificationUtil = FirebaseNotificationUtil(gameId)
        notificationUtil.sendBankTransactionNotification(paymentAmount, auth.uid, BankTransactionEnums.CREDIT)
    }

    companion object {
        private const val TAG = "Fragment Bank Credit"
        private const val CLICKED_PAY = "User clicked Confirm button"
    }
}