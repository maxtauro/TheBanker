package com.maxtauro.monopolywallet

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText

/**
 * TODO add authoring, date, and desc
 */
class DialogFragmentCreateGame: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity!!.layoutInflater

        var layout = inflater.inflate(R.layout.dialog_create_game, null)

        val hostNameInput = layout.findViewById<EditText>(R.id.host_name_input)

        builder.setView(layout)
                .setPositiveButton("Create Game") { dialog, whichButton ->

                    val hostNameInputTxt = hostNameInput.text.toString()

                    val callingActivity = activity as StartPage?
                    callingActivity?.createGame(hostNameInputTxt)
                }
                .setNegativeButton("Cancel") { dialog, whichButton ->
                    dismiss()
                }

        return builder.create()
    }
}