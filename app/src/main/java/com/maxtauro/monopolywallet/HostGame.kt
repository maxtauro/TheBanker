package com.maxtauro.monopolywallet

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.FirebaseReferenceUtil
import com.maxtauro.monopolywallet.util.IntentExtrasConstants
import com.maxtauro.monopolywallet.util.NotificationTypes.HostNotification
import kotlinx.android.synthetic.main.activity_host_game.*
import kotlinx.android.synthetic.main.app_bar_host_game.*

/**
 *  Activity for the Game from the Host user
 */
class HostGame :  AppCompatActivity() {

    //Firebase
    lateinit var firebaseReferenceUtil: FirebaseReferenceUtil
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var auth: FirebaseAuth

    //Player RecyclerView
    lateinit var playerListAdapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var playerListRecyclerView: RecyclerView
    lateinit var playerListlayoutManager: RecyclerView.LayoutManager

    //Host Notifications RecyelerView
    lateinit var hostNotificationListAdapter: FirebaseRecyclerAdapter<HostNotification, HostNotificationListViewHolder>
    lateinit var hostNotificationListRecyclerView: RecyclerView
    lateinit var hostNotificationListLayoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_game)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }


    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()
        setupUtils()
        setupButtons()
        setupGame()
//        setupNotificationService()
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_get_from_bank)
        btnCancel.setOnClickListener {

        }

        val btnStart = findViewById<Button>(R.id.btn_pay_bank)
        btnStart.setOnClickListener {
            TODO("IMPLEMENT BANK PAYMENT AS THE HOST")
        }
    }

    private fun setupUtils() {
        val gameId: String = intent.extras[IntentExtrasConstants.GAME_ID_EXTRA] as String //TODO, pass whole game dao in extras

        firebaseHelper = FirebaseHelper(gameId)
        firebaseReferenceUtil = FirebaseReferenceUtil(gameId)
    }

    private fun setupGame() {

        var txtPlayerBalance: TextView = findViewById(R.id.txt_player_balance)
        var playerBalanceString = getString(R.string.txt_player_balance)

        val playerBalanceRef = firebaseReferenceUtil.getPlayerBalanceRef(auth.uid)

        val balanceListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtPlayerBalance.text = playerBalanceString + dataSnapshot.value
            }

            override fun onCancelled(dbError: DatabaseError) {
                TODO("Have not implemented error handling if cannot receive balance") //To change body of created functions use File | Settings | File Templates.
            }

        }

        playerBalanceRef.addValueEventListener(balanceListener)


        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        playerListInit()
        hostNotificationListInit()
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {

        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        return layoutManager
    }

    private fun hostNotificationListInit() {
        hostNotificationListRecyclerView = findViewById<RecyclerView>(R.id.host_notification_list_recycler)
        hostNotificationListRecyclerView.setHasFixedSize(true)

        hostNotificationListLayoutManager = createLayoutManager()
        hostNotificationListRecyclerView.layoutManager = hostNotificationListLayoutManager

        val options = FirebaseRecyclerOptions.Builder<HostNotification>()
                .setQuery(firebaseHelper.hostNotificationListRef, HostNotification::class.java)
                .build()

        hostNotificationListAdapter = object : FirebaseRecyclerAdapter<HostNotification, HostNotificationListViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostNotificationListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_host_notifications, parent, false)

                return HostNotificationListViewHolder(view)
            }

            override fun onBindViewHolder(holder: HostNotificationListViewHolder, position: Int, notification: HostNotification) {
                holder.hostNotification = notification

                holder.txt_amount.text = notification.amount.toString()
                holder.txt_notification_type.text = notification.NOTIFICATION_TYPE.toString()
                holder.txt_player_id.text = notification.playerId //TODO, actual user name needs to be a parameter
            }
        }

        hostNotificationListAdapter.startListening()
        hostNotificationListRecyclerView.adapter = hostNotificationListAdapter
    }

    private fun playerListInit() {
        playerListRecyclerView = findViewById<RecyclerView>(R.id.playerList)
        playerListRecyclerView.setHasFixedSize(true)

        playerListlayoutManager = createLayoutManager()
        playerListRecyclerView.layoutManager = playerListlayoutManager

        val options = FirebaseRecyclerOptions.Builder<Player>()
                .setQuery(firebaseHelper.playerListRef, Player::class.java)
                .build()

        playerListAdapter = object : FirebaseRecyclerAdapter<Player, PlayerListViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_player_layout, parent, false)

                return PlayerListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int, player: Player) {
                holder.txtPlayerName.text = player.playerName
            }
        }

        playerListAdapter.startListening()
        playerListRecyclerView.adapter = playerListAdapter
    }


}