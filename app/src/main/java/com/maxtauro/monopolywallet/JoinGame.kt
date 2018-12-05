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
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentBankCredit
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentBankDebit
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentPlayerTransaction
import com.maxtauro.monopolywallet.DialogFragments.DialogTransactionConfirmation
import com.maxtauro.monopolywallet.ListViewHolder.PlayerGameNotificationsListViewHolder
import com.maxtauro.monopolywallet.ListViewHolder.PlayerListViewHolder
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.FirebaseReferenceUtil
import com.maxtauro.monopolywallet.util.IntentExtrasConstants
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import kotlinx.android.synthetic.main.activity_host_game.*
import kotlinx.android.synthetic.main.app_bar_host_game.*

/**
 * Activity for the Game from the Non-Host user
 */
class JoinGame :  AppCompatActivity() {

    //Firebase
    private lateinit var firebaseReferenceUtil: FirebaseReferenceUtil
    private lateinit var firebaseHelper: FirebaseHelper
    lateinit var auth: FirebaseAuth

    //RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    //Notifications RecyelerView
    lateinit var playerGameNotificationsListAdapter: FirebaseRecyclerAdapter<PlayerGameNotification, PlayerGameNotificationsListViewHolder>
    lateinit var playerGameNotificationListRecyclerView: RecyclerView
    lateinit var playerGameNotificationListLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

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
        val btnGetFromBank = findViewById<Button>(R.id.btn_get_from_bank)
        btnGetFromBank.setOnClickListener {
            val dialogFragmentBankCredit = DialogFragmentBankCredit()

            val bundle = Bundle()
            bundle.putString(IntentExtrasConstants.GAME_ID_EXTRA, firebaseHelper.gameId)
            dialogFragmentBankCredit.arguments = bundle
            dialogFragmentBankCredit.show(supportFragmentManager, "DialogFragmentBankCredit")
        }

        val btnPayBank = findViewById<Button>(R.id.btn_pay_bank)
        btnPayBank.setOnClickListener {
            val dialogFragmentPayBank = DialogFragmentBankDebit()

            val bundle = Bundle()
            bundle.putString(IntentExtrasConstants.GAME_ID_EXTRA, firebaseHelper.gameId)
            dialogFragmentPayBank.arguments = bundle
            dialogFragmentPayBank.show(supportFragmentManager, "DialogFragmentBankDebit")
        }
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
        notificationListInit()
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {

        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        return layoutManager
    }

    private fun setupUtils() {
        val gameId: String = intent.extras[IntentExtrasConstants.GAME_ID_EXTRA] as String //TODO, pass whole game dao in extras

        firebaseHelper = FirebaseHelper(gameId)
        firebaseReferenceUtil = FirebaseReferenceUtil(gameId)
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
                holder.playerId = player.playerId
                holder.itemView.setOnClickListener {
                    val dialogFragmentPlayerTransaction = DialogFragmentPlayerTransaction()

                    val bundle = Bundle()
                    bundle.putString(IntentExtrasConstants.GAME_ID_EXTRA, firebaseHelper.gameId)
                    bundle.putString(IntentExtrasConstants.RECIPIENT_ID_EXTRA, player.playerId)
                    dialogFragmentPlayerTransaction.arguments = bundle
                    dialogFragmentPlayerTransaction.show(supportFragmentManager, "DialogFragmentBankDebit")
                }
            }
        }

        adapter.startListening()
        listPlayersRecyclerView.adapter = adapter
    }

    private fun notificationListInit() {
        playerGameNotificationListRecyclerView = findViewById<RecyclerView>(R.id.non_host_notification_list_recycler)
        playerGameNotificationListRecyclerView.setHasFixedSize(true)

        playerGameNotificationListLayoutManager = createLayoutManager()
        playerGameNotificationListRecyclerView.layoutManager = playerGameNotificationListLayoutManager

        val options = FirebaseRecyclerOptions.Builder<PlayerGameNotification>()
                .setQuery(firebaseReferenceUtil.getPlayerNotificationRef(auth.uid!!), PlayerGameNotification::class.java)
                .build()

        playerGameNotificationsListAdapter = object : FirebaseRecyclerAdapter<PlayerGameNotification, PlayerGameNotificationsListViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerGameNotificationsListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_user_notifications, parent, false)

                return PlayerGameNotificationsListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerGameNotificationsListViewHolder, position: Int, notification: PlayerGameNotification) {
                holder.playerGameNotifications = notification

                holder.txt_amount.text = notification.amount.toString()
                holder.txt_notification_type.text = notification.notificationType.toString()
                holder.txt_player_id.text = notification.playerId //TODO, actual user name needs to be a parameter
                holder.itemView.setOnClickListener {
                    DialogTransactionConfirmation.newInstance(notification).show(supportFragmentManager, "DialogTransactionConfirmation")
                }
            }
        }

        playerGameNotificationsListAdapter.startListening()
        playerGameNotificationListRecyclerView.adapter = playerGameNotificationsListAdapter
    }

}