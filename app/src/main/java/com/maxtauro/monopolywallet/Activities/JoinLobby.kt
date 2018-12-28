package com.maxtauro.monopolywallet.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.maxtauro.monopolywallet.GameDao
import com.maxtauro.monopolywallet.ListViewHolder.PlayerListViewHolder
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.Firebase.FirebaseHelper
import com.maxtauro.monopolywallet.util.TopicSubscriptionUtil

/**
 * TODO add authoring, date, and desc
 */
class JoinLobby:  AppCompatActivity() {

    lateinit var firebaseHelper : FirebaseHelper
    private lateinit var auth: FirebaseAuth

    lateinit var game : GameDao

    //RecyclerView
    lateinit var adapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_lobby)

        //TODO do I even want this here?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }
    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()


        setupGame()
        setupNotificationServices()
        playerListInit()
    }

    private fun setupGame() {

        val playerName = intent.extras["playerName"] as String
        val gameId = intent.extras["gameId"] as String

        firebaseHelper = FirebaseHelper(gameId)

        var txtGameId: TextView = findViewById(R.id.txt_game_id) //TODO maybe pull UI elements into another method
        txtGameId.text = "Game #: $gameId"

        firebaseHelper.setDisplayName(playerName)
        firebaseHelper.joinGame(gameId, playerName)

        FirebaseMessaging.getInstance().subscribeToTopic(gameId)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d("JoinLobby" , msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

        val btnStart = findViewById<Button>(R.id.btn_leave)
        btnStart.setOnClickListener {
            firebaseHelper.leaveGame(auth.uid!!)
            finish()
        }
    }

    private fun setupNotificationServices() {
        val gameId = firebaseHelper.gameId

        val topicSubscriptionUtil = TopicSubscriptionUtil()

        // Subscribe To Game Notifications
        topicSubscriptionUtil.subscribeToTopic(gameId)

        //Subscribe To Personal Topic
        val personalTopic = gameId + auth.uid
        topicSubscriptionUtil.subscribeToTopic(personalTopic)
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