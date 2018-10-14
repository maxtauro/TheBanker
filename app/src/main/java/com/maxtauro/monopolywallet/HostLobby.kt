package com.maxtauro.monopolywallet

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter

class HostLobby :  AppCompatActivity() {

    lateinit var firebaseHelper: FirebaseHelper

    //RecyclerView
    lateinit var adapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_lobby)

        setupButtons()
        setupGame()
    }

    private fun setupGame() {
        val gameId = GameBank.generateRandomId()

        var txtGameId: TextView = findViewById(R.id.txt_game_id) //TODO maybe pull UI elements into another method
        txtGameId.text = "Game #: $gameId"

        firebaseHelper = FirebaseHelper(gameId)

        firebaseHelper.createGame(gameId, "Host name to input") // TODO enter a host name
        firebaseHelper.joinGame(gameId, "Host name to input")
        playerListInit()
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
//            firebaseHelper.deleteGame() TODO
            finish()
        }

        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            TODO("Need to implemenet start method")
        }
    }

    private fun playerListInit() {

        listPlayersRecyclerView = findViewById<RecyclerView>(R.id.playerList)
        listPlayersRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        listPlayersRecyclerView.layoutManager = layoutManager

        adapter = object : FirebaseRecyclerAdapter<Player, PlayerListViewHolder>(
                Player::class.java,
                R.layout.list_holder_player_layout,
                PlayerListViewHolder::class.java,
                firebaseHelper.playerListRef
        ) {
            override fun populateViewHolder(viewHolder : PlayerListViewHolder, player : Player, position : Int) {
                viewHolder.txtPlayerName.setText(player.playerName)

                fun onClick(view: View, position: Int) {

                }
            }
        }

        adapter.notifyDataSetChanged()
        listPlayersRecyclerView.adapter = adapter
    }


}
