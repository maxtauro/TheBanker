package com.maxtauro.monopolywallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter

class JoinLobby:  AppCompatActivity() {

    lateinit var firebaseHelper : FirebaseHelper
    lateinit var game : GameBank

    //RecyclerView
    lateinit var adapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_lobby)

        setupGame()
        playerListInit()
    }

    private fun setupGame() {

        val playerName = intent.extras["playerName"] as String
        val gameId = intent.extras["gameId"] as String

        firebaseHelper = FirebaseHelper(gameId)

        var txtGameId: TextView = findViewById(R.id.txt_game_id) //TODO maybe pull UI elements into another method
        txtGameId.text = "Game #: $gameId"

        firebaseHelper.joinGame(gameId, playerName)

        val btnStart = findViewById<Button>(R.id.btn_leave)
        btnStart.setOnClickListener {
            firebaseHelper.leaveGame(playerName)
            finish()
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