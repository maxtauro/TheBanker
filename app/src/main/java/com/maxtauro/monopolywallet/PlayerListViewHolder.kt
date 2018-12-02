package com.maxtauro.monopolywallet

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentBankDebit
import com.maxtauro.monopolywallet.util.IntentExtrasConstants

/**
 * TODO add authoring, date, and desc
 */
class PlayerListViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    var txtPlayerName: TextView
    lateinit var playerId: String

    constructor(itemView : View) : super(itemView) {
        txtPlayerName = itemView.findViewById(R.id.txt_player_name);
        itemView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {


        Log.d("PlayerListViewHolder", "Click on player")
    }
}