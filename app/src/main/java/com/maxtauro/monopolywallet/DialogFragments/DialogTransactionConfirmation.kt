package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.PlayerGameNotificationUtil
import android.widget.LinearLayout



class DialogTransactionConfirmation: DialogFragment()  {

    lateinit var transactionNotification: PlayerGameNotification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogTransactionConfirmation = AlertDialog.Builder(activity).create()

        // Labels
        val confirmBtnLabel: String = getString(R.string.confirm_transaction_label)
        val declineBtnLabel: String = getString(R.string.decline_transaction_label)

        dialogTransactionConfirmation.setButton(AlertDialog.BUTTON_POSITIVE, confirmBtnLabel) { _, _ ->
                    Log.d(TAG, CLICKED_CONFIRM)
                    PlayerGameNotificationUtil.confirmNotification(transactionNotification)
                    Log.d(TAG, INPUT_SUCCESS)
                }
        dialogTransactionConfirmation.setButton(AlertDialog.BUTTON_NEGATIVE, declineBtnLabel) { _, _ ->
                    Log.d(TAG, CLICKED_DECLINE)
                    PlayerGameNotificationUtil.declineNotification(transactionNotification)
                }

        dialogTransactionConfirmation.show()

        val btnPositive = dialogTransactionConfirmation.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative = dialogTransactionConfirmation.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams

        return dialogTransactionConfirmation
    }

    private fun setNotification(notification: PlayerGameNotification) {
        transactionNotification = notification
    }

    companion object {

        fun newInstance(notification: PlayerGameNotification): DialogTransactionConfirmation {
            val args: Bundle = Bundle()
            val fragment = DialogTransactionConfirmation()
            fragment.arguments = args
            fragment.setNotification(notification)
            return fragment
        }

        private const val TAG = "TransactionConfirmation"
        private const val CLICKED_CONFIRM = "User clicked confirm button"
        private const val CLICKED_DECLINE = "User clicked decline button"
        private const val INPUT_SUCCESS = "Valid Input, dismissing Fragment"
    }
}