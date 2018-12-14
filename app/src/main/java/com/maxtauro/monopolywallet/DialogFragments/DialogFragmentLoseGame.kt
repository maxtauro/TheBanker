package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.maxtauro.monopolywallet.Activities.HostGameActivity
import com.maxtauro.monopolywallet.Activities.NonHostGameActivity
import com.maxtauro.monopolywallet.Constants.IntentExtrasConstants
import com.maxtauro.monopolywallet.R

class DialogFragmentLoseGame: DialogFragment() {

    var isHost = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments != null) {
            isHost = arguments!!.getBoolean(IntentExtrasConstants.IS_HOST_EXTRA,false)
        }

        val builder = AlertDialog.Builder(activity)

        val loseMessage =
                if (isHost) getString(R.string.host_lose_string)
                else getString(R.string.non_host_lose_string)

        builder.setMessage(loseMessage)
        builder.setNeutralButton(R.string.ok_button_label) { _: DialogInterface, _: Int -> }

        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (isHost) {
            enableHostGameLostMode()
        }

        else {
            nonHostLeaveGame()
        }

        super.onDismiss(dialog)
    }

    private fun nonHostLeaveGame() {
        val callingActivity = activity as NonHostGameActivity?
        callingActivity?.nonHostLeaveGame()
    }

    private fun enableHostGameLostMode() {
        val callingActivity = activity as HostGameActivity?
        callingActivity?.enableHostGameLostMode()
    }
}