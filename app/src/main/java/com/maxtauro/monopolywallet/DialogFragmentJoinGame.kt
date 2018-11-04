package com.maxtauro.monopolywallet

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * TODO add authoring, date, and desc
 */
class DialogFragmentJoinGame : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity!!.layoutInflater

        var layout = inflater.inflate(R.layout.dialog_join_game, null)

        val gameIdInput = layout.findViewById<EditText>(R.id.game_id_input)
        val playerNameInput = layout.findViewById<EditText>(R.id.nickname_input)


        builder.setView(layout)
            .setPositiveButton("Join") { dialog, whichButton ->

                val gameIdInputTxt = gameIdInput.text.toString()
                val playerNameInputTxt = playerNameInput.text.toString()

                val callingActivity = activity as StartPage?
                callingActivity?.joinGameAsync(gameIdInputTxt, playerNameInputTxt)

            }
            .setNegativeButton("Cancel") { dialog, whichButton ->
                dismiss()
            }

        return builder.create()

    }

}


