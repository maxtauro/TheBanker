package com.maxtauro.monopolywallet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.messaging.FirebaseMessaging
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil


class HostLobby :  AppCompatActivity() {

    lateinit var firebaseHelper: FirebaseHelper

    //RecyclerView
    lateinit var adapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_lobby)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }

        setupButtons()
        setupGame()
    }

    private fun setupGame() {
        val gameId = GameBank.generateRandomId()
        val hostName = intent.extras["hostName"] as String

        var txtGameId: TextView = findViewById(R.id.txt_game_id) //TODO maybe pull UI elements into another method
        txtGameId.text = "Game #: $gameId"

        firebaseHelper = FirebaseHelper(gameId)

        firebaseHelper.createGame(gameId, hostName) // TODO enter a host name
        firebaseHelper.joinGame(gameId, hostName)

        FirebaseMessaging.getInstance().subscribeToTopic(gameId)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d("JoinLobby" , msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }


        playerListInit()
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            firebaseHelper.deleteGame()
            finish()
        }

        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            var notificationUtil = FirebaseNotificationUtil()
            notificationUtil.sendNotificationToUser(firebaseHelper.gameId, "Test from host")
            //TODO("Need to implemenet start method")
        }
    }

    private fun playerListInit() {

        listPlayersRecyclerView = findViewById<RecyclerView>(R.id.playerList)
        listPlayersRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        listPlayersRecyclerView.layoutManager = layoutManager

        val options = FirebaseRecyclerOptions.Builder<Player>()
                .setQuery(firebaseHelper.playerListRef, Player::class.java)
                .build()


        adapter = object : FirebaseRecyclerAdapter<Player, PlayerListViewHolder>(options) {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_player_layout, parent, false)

                return PlayerListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int, player: Player) {
                holder.txtPlayerName.text = player.playerName
//                holder.root.setOnClickListener(View.OnClickListener { Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_SHORT).show() })
            }

        }

        adapter.startListening()
        listPlayersRecyclerView.adapter = adapter
    }


}
