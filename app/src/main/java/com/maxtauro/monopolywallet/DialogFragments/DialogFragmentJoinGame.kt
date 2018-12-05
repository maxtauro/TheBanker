package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.Activities.StartPage
import com.maxtauro.monopolywallet.util.validate

/**
 * DialogFragment class for joining a game lobby
 */
class DialogFragmentJoinGame : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity!!.layoutInflater

        var layout = inflater.inflate(R.layout.dialog_join_game, null)

        val gameIdInput = layout.findViewById<EditText>(R.id.game_id_input)
        val playerNameInput = layout.findViewById<EditText>(R.id.nickname_input)

        //UI Labels
        val joinBtnLabel: String = getString(R.string.join_game_button_label)

        //Strings for messages
        val invalidGameIdFormatMsg: String = getString(R.string.invalid_game_id_format_msg)
        val invalidPlayerNameMessage: String = getString(R.string.invalid_name_msg)

        // TODO, only validate on submit
        gameIdInput.validate({ s -> isValidGameIdInput(s) }, invalidGameIdFormatMsg)
        playerNameInput.validate({ s -> s.isNotEmpty() && s.length < 255 }, invalidPlayerNameMessage)

        builder.setView(layout)
                .setPositiveButton(joinBtnLabel) { dialog, whichButton ->

                    Log.d(TAG, CLICKED_JOIN)
                    val gameIdInputTxt = gameIdInput.text.toString()
                    val playerNameInputTxt = playerNameInput.text.toString()

                    if (gameIdInput.error == null && playerNameInput.error == null) {
                        Log.d(TAG, INPUT_SUCCESS)
                        dismiss()
                        val callingActivity = activity as StartPage?
                        callingActivity?.joinGameAsync(gameIdInputTxt, playerNameInputTxt)
                    }
                }
                .setNegativeButton("Cancel") { _, _ -> }

        return builder.create()

    }

    private fun isValidGameIdInput(gameIdInput: String): Boolean {
        val isCorrectLength = gameIdInput.length == 6
        val isAlphaNumeric = gameIdInput.matches(Regex("[A-Za-z0-9]+"))

        return isCorrectLength && isAlphaNumeric
    }

    companion object {
        private const val TAG = "DialogFragmentJoinGame"
        private const val CLICKED_JOIN = "User clicked Join button"
        private const val INPUT_SUCCESS = "Valid Input, dismissing Fragment"
    }
}


