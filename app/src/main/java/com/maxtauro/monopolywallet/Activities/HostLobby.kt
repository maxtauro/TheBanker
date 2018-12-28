package com.maxtauro.monopolywallet.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.maxtauro.monopolywallet.GameDao
import com.maxtauro.monopolywallet.ListViewHolder.PlayerListViewHolder
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.Firebase.FirebaseHelper
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil
import com.maxtauro.monopolywallet.util.TopicSubscriptionUtil

/**
 * TODO add authoring, date, and desc
 */
class HostLobby :  AppCompatActivity() {

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var auth: FirebaseAuth

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
    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()

        setupButtons()
        setupGame()
        setupNotificationService()
    }

    private fun setupNotificationService() {

        val gameId = firebaseHelper.gameId

        val topicSubscriptionUtil = TopicSubscriptionUtil()

        // Subscribe To Game Notifications
        topicSubscriptionUtil.subscribeToTopic(gameId)

        //Subscribe To Personal Topic
        val personalTopic = gameId + auth.uid
        topicSubscriptionUtil.subscribeToTopic(personalTopic)

        // Subscribe To Game Host Topic
        val hostTopic = "$gameId-Host"
        topicSubscriptionUtil.subscribeToTopic(hostTopic)
    }

    private fun setupGame() {
        val gameId = GameDao.generateRandomId()
        val hostName = intent.extras["hostName"] as String

        var txtGameId: TextView = findViewById(R.id.txt_game_id)
        txtGameId.text = "Game #: $gameId"

        firebaseHelper = FirebaseHelper(gameId)

        firebaseHelper.setDisplayName(hostName)
        firebaseHelper.createGame(hostName)
        firebaseHelper.joinGame(gameId, hostName)

        playerListInit()
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_pay_bank)
        btnCancel.setOnClickListener {
            firebaseHelper.deleteGame()
            finish()
        }

        val btnStart = findViewById<Button>(R.id.btn_get_from_bank)
        btnStart.setOnClickListener {
            val notificationUtil = FirebaseNotificationUtil(firebaseHelper.gameId)
            notificationUtil.startGame()
            firebaseHelper.startGame()
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
