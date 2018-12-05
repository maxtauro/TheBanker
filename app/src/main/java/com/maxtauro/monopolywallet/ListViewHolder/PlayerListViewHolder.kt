package com.maxtauro.monopolywallet.ListViewHolder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.maxtauro.monopolywallet.R

/**
 * ListViewHolder for players in the current game/game lobby
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