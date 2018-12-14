package com.maxtauro.monopolywallet.Activities

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.maxtauro.monopolywallet.Constants.IntentExtrasConstants
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentBankCredit
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentBankDebit
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentLoseGame
import com.maxtauro.monopolywallet.DialogFragments.DialogFragmentPlayerTransaction
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

        isHost = true

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //setupGameStatusListener()
    }

    fun enableHostGameLostMode() {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}