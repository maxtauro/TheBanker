package com.maxtauro.monopolywallet.DialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.StartPage
import com.maxtauro.monopolywallet.util.validate

/**
 * Dialog Fragment for creating/hosting A Game Lobby
 */
class DialogFragmentCreateGame: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val layout = inflater.inflate(R.layout.dialog_create_game, null)

        // Labels
        val createGameBtnLabel: String = getString(R.string.create_game_button_label)
        val cancelBtnLabel: String = getString(R.string.cancel_button_label)

        val hostNameInput = layout.findViewById<EditText>(R.id.host_name_input)
        val invalidNameMsg = getString(R.string.invalid_name_msg)

        hostNameInput.validate({s -> s.isNotEmpty() && s.length < 255}, invalidNameMsg)

        builder.setView(layout)
                .setPositiveButton(createGameBtnLabel) { _, _ ->
                    Log.d(TAG, CLICKED_CREATE)

                    val hostNameInputTxt = hostNameInput.text.toString()

                    val callingActivity = activity as StartPage?
                    callingActivity?.createGame(hostNameInputTxt)
                    Log.d(TAG, INPUT_SUCCESS)
                }
                .setNegativeButton(cancelBtnLabel) { _, _ ->}

        return builder.create()
    }

    companion object {
        private const val TAG = "Fragment CreateGame"
        private const val CLICKED_CREATE = "User clicked Create button"
        private const val INPUT_SUCCESS = "Valid Input, dismissing Fragment"
    }
}