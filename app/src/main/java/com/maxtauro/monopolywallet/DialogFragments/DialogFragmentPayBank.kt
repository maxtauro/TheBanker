package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil
import com.maxtauro.monopolywallet.util.IntentExtrasConstants


/**
 * Dialog Fragment for sending a payment to the Bank
 */
class DialogFragmentPayBank: DialogFragment() {

    lateinit var gameId: String
    private lateinit var auth: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        auth = FirebaseAuth.getInstance()

        if (arguments != null) {
            gameId = arguments!!.getString(IntentExtrasConstants.GAME_ID_EXTRA,"")
        }
        
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pay_bank, null)

        // Labels
        val payBankBtnLabel: String = getString(R.string.pay_bank_button_label)
        val cancelBtnLabel: String = getString(R.string.cancel_button_label)

        val paymentAmountInput = layout.findViewById<EditText>(R.id.payment_amount_input)

        var paymentAmount: Int = 0

        builder.setView(layout)
                .setPositiveButton(payBankBtnLabel) { _, _ ->

                    try {
                        val paymentAmountStr = paymentAmountInput.text.toString()
                        paymentAmount = paymentAmountStr.toInt()
                    }
                    catch (e: NumberFormatException) {TODO("Validate amount input")}

                    Log.d(TAG, CLICKED_PAY)

                    val notificationUtil = FirebaseNotificationUtil(gameId)
                    notificationUtil.sendBankPaymentNotification(paymentAmount, auth.uid)

                }
                .setNegativeButton(cancelBtnLabel) { _, _ ->}

        return builder.create()
    }

    companion object {
        private const val TAG = "Fragment CreateGame"
        private const val CLICKED_PAY = "User clicked Pay button"
    }

}