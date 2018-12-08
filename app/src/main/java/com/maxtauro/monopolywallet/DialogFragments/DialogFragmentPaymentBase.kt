package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.Constants.IntentExtrasConstants

/**
 * Base class for selecting payment amounts
 */
abstract class DialogFragmentPaymentBase: DialogFragment() {

    lateinit var gameId: String
    protected lateinit var auth: FirebaseAuth

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

        setUpRadioGroup(layout)

        builder.setView(layout)
                .setPositiveButton(payBankBtnLabel) { _, _ ->

                    try {
                        val paymentAmountStr = paymentAmountInput.text.toString()
                        paymentAmount = paymentAmountStr.toInt()
                    }
                    catch (e: NumberFormatException) {TODO("Validate amount input")}

                    Log.d(TAG, CLICKED_PAY)
                    onPositiveButtonClick(paymentAmount)

                }
                .setNegativeButton(cancelBtnLabel) { _, _ ->}

        return builder.create()
    }

    // This function is to be overridden in DialogFragmentPlayerTransaction
    protected open fun setUpRadioGroup(layout: View) {}

    abstract fun onPositiveButtonClick(paymentAmount: Int)

    companion object {
        private const val TAG = "Fragment CreateGame"
        private const val CLICKED_PAY = "User clicked Pay button"
    }

}