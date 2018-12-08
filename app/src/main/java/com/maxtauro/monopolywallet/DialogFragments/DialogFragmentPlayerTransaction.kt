package com.maxtauro.monopolywallet.DialogFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import com.maxtauro.monopolywallet.Constants.IntentExtrasConstants
import com.maxtauro.monopolywallet.Constants.PlayerTransactionEnum
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.*

/**
 * Dialog to input p2p transaction requests
 */

class DialogFragmentPlayerTransaction: DialogFragmentPaymentBase() {

    lateinit var recipientId: String
    var tranType = PlayerTransactionEnum.REQUEST_MONEY

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments != null) {
            recipientId = arguments!!.getString(IntentExtrasConstants.RECIPIENT_ID_EXTRA,"")
        }

        return super.onCreateDialog(savedInstanceState)
    }

    override fun setUpRadioGroup(layout: View) {
        val radioGroup = layout.findViewById<RadioGroup>(R.id.tran_type_radio_group)
        radioGroup.visibility = View.VISIBLE

        radioGroup.setOnCheckedChangeListener { _, checkedId -> onRadioButtonClicked(checkedId)

        }
    }

    override fun onPositiveButtonClick(paymentAmount: Int) {
        val notificationUtil = FirebaseNotificationUtil(gameId)
        val firebaseHelper = FirebaseHelper(gameId)

        if (auth.uid == recipientId) return

        when (tranType) {
            PlayerTransactionEnum.SEND_MONEY -> {
                notificationUtil.sendSendMoneyNotification(paymentAmount, auth.uid, recipientId)
                firebaseHelper.createSendMoneyRequest(paymentAmount, auth.uid, recipientId)
            }

            PlayerTransactionEnum.REQUEST_MONEY -> {
                notificationUtil.sendRequestMoneyNotification(paymentAmount, auth.uid, recipientId)
                firebaseHelper.createRequestMoneyRequest(paymentAmount, auth.uid, recipientId)
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun onRadioButtonClicked(checkedId: Int) {
        when (checkedId) {
            R.id.radio_request_money ->
                tranType = PlayerTransactionEnum.REQUEST_MONEY
            R.id.radio_send_money ->
                tranType = PlayerTransactionEnum.SEND_MONEY
        }

        Log.d(TAG, "Selected tranType: $tranType")
    }

    private companion object {

        private const val TAG = "DialogFragmentPlayerTransaction"
    }
}