package com.maxtauro.monopolywallet.Activities

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.maxtauro.monopolywallet.Constants.FirebaseReferenceConstants
import com.maxtauro.monopolywallet.ListViewHolder.PlayerListViewHolder
import com.maxtauro.monopolywallet.Player
import com.maxtauro.monopolywallet.R
import kotlinx.android.synthetic.main.activity_host_game.*
import kotlinx.android.synthetic.main.app_bar_host_game.*

/**
 *  Activity for the Game from the Host user
 */
class HostGameActivity:  GameActivity() {

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
        isHost = true
        setupGameStatusListener()
    }

    fun enableHostGameLostMode() {
        val txtPlayerBalance: TextView = findViewById(R.id.txt_player_balance)
        txtPlayerBalance.visibility = View.INVISIBLE

        removeButtons()
        disablePlayerTransactions()
    }

    private fun disablePlayerTransactions() {
        playerListAdapter = object : FirebaseRecyclerAdapter<Player, PlayerListViewHolder>(playerListOptions) {

            //TODO put repeated setup methods into own functions
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_holder_player_layout, parent, false)

                return PlayerListViewHolder(view)
            }

            override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int, player: Player) {
                    holder.txtPlayerName.text = player.playerName
                    holder.playerId = player.playerId
                    //TODO maybe show player balance
            }
        }

        playerListAdapter.startListening()
        listPlayersRecyclerView.adapter = playerListAdapter
    }

    private fun removeButtons() {
        val btnGetFromBank = findViewById<Button>(R.id.btn_get_from_bank)
        btnGetFromBank.visibility = View.GONE

        val btnPayBank = findViewById<Button>(R.id.btn_pay_bank)
        btnPayBank.visibility = View.GONE
    }

    private fun setupGameStatusListener() {

        val playerListRef = firebaseHelper.playerListRef.orderByChild(FirebaseReferenceConstants.PLAYER_ACTIVE_NODE_KEY).equalTo(true)

        val playerCountListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val activePlayers = dataSnapshot.childrenCount

                if (activePlayers == 1L) {
                    val winnerInfo = getWinnerInfo(dataSnapshot)
                    firbaseNotificationUtil.sendEndGameNotification(winnerInfo)
                }
            }

            override fun onCancelled(dbError: DatabaseError) {
                TODO("Implement Db error handling") //To change body of created functions use File | Settings | File Templates.
            }
        }

        playerListRef.addValueEventListener(playerCountListener)
    }

    //TODO there has to be a simpler way to parse this dataSnapshot
    private fun getWinnerInfo(dataSnapshot: DataSnapshot): Player {
        val winnerId = ArrayList((dataSnapshot.value as HashMap<*,*>).keys)[0] as String
        val winnerInfoMap = dataSnapshot.child(winnerId).value as java.util.HashMap<*, *>
        val winnerName = (dataSnapshot.child(winnerId).value as java.util.HashMap<*, *>)["playerName"].toString()

        return Player(winnerId, winnerName)
    }
}