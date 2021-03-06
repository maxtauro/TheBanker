package com.maxtauro.monopolywallet.Activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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
import com.maxtauro.monopolywallet.Constants.FirebaseReferenceConstants
import com.maxtauro.monopolywallet.ListViewHolder.PlayerGameNotificationsListViewHolder
import com.maxtauro.monopolywallet.ListViewHolder.PlayerListViewHolder
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.Firebase.FirebaseHelper
import com.maxtauro.monopolywallet.util.FirebaseReferenceUtil
import com.maxtauro.monopolywallet.Constants.IntentExtrasConstants
import com.maxtauro.monopolywallet.DialogFragments.*
import com.maxtauro.monopolywallet.util.FirebaseNotificationUtil
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import android.support.v7.widget.DividerItemDecoration



abstract class GameActivity: AppCompatActivity() {

    var isHost = false

    //Firebase util classes
    protected lateinit var firebaseReferenceUtil: FirebaseReferenceUtil
    protected lateinit var firebaseHelper: FirebaseHelper
    protected lateinit var firbaseNotificationUtil: FirebaseNotificationUtil
    lateinit var auth: FirebaseAuth

    //RecyclerView
    protected lateinit var playerListAdapter: FirebaseRecyclerAdapter<Player, PlayerListViewHolder>
    protected lateinit var listPlayersRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var playerListOptions: FirebaseRecyclerOptions<Player>
    lateinit var notificationListOptions:  FirebaseRecyclerOptions<PlayerGameNotification>

    //Notifications RecyclerView
    lateinit var playerGameNotificationsListAdapter: FirebaseRecyclerAdapter<PlayerGameNotification, PlayerGameNotificationsListViewHolder>
    lateinit var playerGameNotificationListRecyclerView: RecyclerView
    lateinit var playerGameNotificationListLayoutManager: RecyclerView.LayoutManager

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()

        setupUtils()
        setupButtons()
        setupGame()
    }

    private fun setupUtils() {
        val gameId: String = intent.extras[IntentExtrasConstants.GAME_ID_EXTRA] as String

        firebaseHelper = FirebaseHelper(gameId)
        firebaseReferenceUtil = FirebaseReferenceUtil(gameId)
        firbaseNotificationUtil = FirebaseNotificationUtil(gameId)
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
            val dialogFragmentBankDebit = DialogFragmentBankDebit()

            val bundle = Bundle()
            bundle.putString(IntentExtrasConstants.GAME_ID_EXTRA, firebaseHelper.gameId)
            dialogFragmentBankDebit.arguments = bundle
            dialogFragmentBankDebit.show(supportFragmentManager, "DialogFragmentBankDebit")
        }
    }

    private fun setupGame() {
        var txtPlayerBalance: TextView = findViewById(R.id.txt_player_balance)
        var playerBalanceString = getString(R.string.txt_player_balance)

        val playerBalanceRef = firebaseReferenceUtil.getPlayerBalanceRef(auth.uid)

        val balanceListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtPlayerBalance.text = playerBalanceString + dataSnapshot.value

                if (dataSnapshot.value as Long <= 0) {
                    loseGame()
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                TODO("Have not implemented error handling if cannot receive balance") //To change body of created functions use File | Settings | File Templates.
            }

        }

        playerBalanceRef.addValueEventListener(balanceListener)

        playerListOptions = FirebaseRecyclerOptions.Builder<Player>()
                .setQuery(firebaseHelper.playerListRef.orderByChild(FirebaseReferenceConstants.PLAYER_ACTIVE_NODE_KEY).equalTo(true), Player::class.java)
                .build()
        notificationListOptions = FirebaseRecyclerOptions.Builder<PlayerGameNotification>()
                .setQuery(firebaseReferenceUtil.getPlayerNotificationRef(auth.uid!!), PlayerGameNotification::class.java)
                .build()

        initRecyclerViews()
    }



    private fun initRecyclerViews() {
        playerListInit()
        notificationListInit()
    }

    private fun playerListInit() {
        listPlayersRecyclerView = findViewById<RecyclerView>(R.id.playerList)
        listPlayersRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        listPlayersRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        listPlayersRecyclerView.addItemDecoration(dividerItemDecoration)

        playerListAdapter = object : FirebaseRecyclerAdapter<Player, PlayerListViewHolder>(playerListOptions) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_player_layout, parent, false)

                return PlayerListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int, player: Player) {

                // Users should not see themselves listed in their own game
                if (auth.uid == player.playerId && false) {
                    //TODO fix the bug where all entries disappear on any firebase changes
//                    holder.hideEntry()
                }

                else {
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
        }

        playerListAdapter.startListening()
        listPlayersRecyclerView.adapter = playerListAdapter
    }

    private fun notificationListInit() {
        playerGameNotificationListRecyclerView = findViewById<RecyclerView>(R.id.notification_list_recycler)
        playerGameNotificationListRecyclerView.setHasFixedSize(true)

        playerGameNotificationListLayoutManager = createLayoutManager()
        playerGameNotificationListRecyclerView.layoutManager = playerGameNotificationListLayoutManager

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        playerGameNotificationListRecyclerView.addItemDecoration(dividerItemDecoration)

        playerGameNotificationsListAdapter = object : FirebaseRecyclerAdapter<PlayerGameNotification, PlayerGameNotificationsListViewHolder>(notificationListOptions) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerGameNotificationsListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_user_notifications, parent, false)

                return PlayerGameNotificationsListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerGameNotificationsListViewHolder, position: Int, notification: PlayerGameNotification) {

                holder.displayNotification(notification, applicationContext)
                holder.itemView.setOnClickListener {
                    DialogTransactionConfirmation.newInstance(notification).show(supportFragmentManager, "DialogTransactionConfirmation")
                }
            }
        }

        playerGameNotificationsListAdapter.startListening()
        playerGameNotificationListRecyclerView.adapter = playerGameNotificationsListAdapter
    }

    private fun createLayoutManager(): RecyclerView.LayoutManager {
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        return layoutManager
    }

    protected open fun loseGame() {
        firebaseHelper.setPlayerIsActive(auth.uid!!, false)

        val dialogFragmentLoseGame = DialogFragmentLoseGame()
        val bundle = Bundle()
        bundle.putBoolean(IntentExtrasConstants.IS_HOST_EXTRA, isHost)
        dialogFragmentLoseGame.arguments = bundle
        dialogFragmentLoseGame.show(supportFragmentManager, "DialogFragmentLoseGame")
    }

    fun returnToStartPage() {
        val startPageIntent = Intent(this, StartPage::class.java)
        startActivity(startPageIntent)
        finish()
    }
}