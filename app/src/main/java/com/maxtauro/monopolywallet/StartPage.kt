package com.maxtauro.monopolywallet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.maxtauro.monopolywallet.util.FirebaseHelper

class StartPage : AppCompatActivity() {

    val dialogJoinGame = DialogFragmentJoinGame()
    val dialogCreateGame = DialogFragmentCreateGame()

    private lateinit var firebaseHelper : FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()

        FirebaseMessaging.getInstance().setAutoInitEnabled(true) //TODO move this into game

    }

    private fun setupButtons() {
        val btnJoin = findViewById<Button>(R.id.join_button)
        btnJoin.setOnClickListener {
            dialogJoinGame.show(supportFragmentManager, "join game dialog")
        }

        val btnStart = findViewById<Button>(R.id.start_button)
        btnStart.setOnClickListener {
            dialogCreateGame.show(supportFragmentManager, "create game dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun createGame(hostName : String) {
        val hostLobbyIntent = Intent(this, HostLobby::class.java)
        hostLobbyIntent.putExtra("hostName", hostName)
        startActivity(hostLobbyIntent)
    }

    fun joinGameAsync(gameId: String, playerName: String) { //TODO think of better name
                                                            //TODO try to move to firebaseHelper
        val playerLobbyIntent = Intent(this, JoinLobby::class.java)

        firebaseHelper = FirebaseHelper(gameId)
        var gameRef = firebaseHelper.databaseRef.child(gameId)

        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!!.exists()) {

                            playerLobbyIntent.putExtra("playerName", playerName) // TODO have the name arg be from some enum
                            playerLobbyIntent.putExtra("gameId", gameId)
                            startActivity(playerLobbyIntent)
                        }
                        else {
                            dialogJoinGame.show(supportFragmentManager, "join game dialog")
                            //TODO add toast like pop up to explain that the game is invalid
                            Log.e("DialogFragment", "Tried to join invalid game")
                        }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}
