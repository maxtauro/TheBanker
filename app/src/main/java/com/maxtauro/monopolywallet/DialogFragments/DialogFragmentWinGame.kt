package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.maxtauro.monopolywallet.Activities.GameActivity
import com.maxtauro.monopolywallet.R


//TODO move this to a Dialog themed activity, showing a dialog from a service is very bad practice
class DialogFragmentWinGame: DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        val winMessage = getString(R.string.winner_message_string)

        builder.setMessage(winMessage)
        builder.setNeutralButton(R.string.ok_button_label) { _: DialogInterface, _: Int -> }

        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        val callingActivity = activity as GameActivity
        callingActivity.returnToStartPage()
    }
}