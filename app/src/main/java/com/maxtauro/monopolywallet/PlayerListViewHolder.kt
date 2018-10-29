package com.maxtauro.monopolywallet

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView

class PlayerListViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    lateinit var txtPlayerName: TextView

    constructor(itemView : View) : super(itemView) {
        txtPlayerName = itemView.findViewById(R.id.txt_player_name);
        itemView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Log.d("PlayerListViewHolder", "Click on player")
    }
}