package com.maxtauro.monopolywallet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.TaskHelper

/**
 * TODO add authoring, date, and desc
 */
class StartPage : AppCompatActivity() {

    val dialogJoinGame = DialogFragmentJoinGame()
    val dialogCreateGame = DialogFragmentCreateGame()

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseHelper : FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        signInToFirebaseAnonymously()

        setupButtons()

        FirebaseMessaging.getInstance().isAutoInitEnabled = true //TODO move this into game

    }

    private fun setupButtons() {
        val btnJoin = findViewById<Button>(R.id.join_button)
        btnJoin.setOnClickListener {
            dialogJoinGame.show(supportFragmentManager, "join game dialog")
        }

        val btnStart = findViewById<Button>(R.id.start_button)
        btnStart.setOnClickListener {
           // dialogCreateGame.show(supportFragmentManager, "create game dialog")

            //TODO REMOVE THIS, TEMP FOR MAKING UI
            val tempHostIntent = Intent(this, JoinGame::class.java)
            tempHostIntent.putExtra("gameId", "7988de")
            startActivity(tempHostIntent)
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
        var gameRef = firebaseHelper.gameRef

        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            //TODO Tidy this up
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //TODO get these ref paths from enum, create a util class that builds the path (builder pattern)
                val isGameActive = (dataSnapshot.child("gameInfo").child("gameActive").value ?: false) as Boolean

                if(dataSnapshot.exists() && !isGameActive) {

                            playerLobbyIntent.putExtra("playerName", playerName) // TODO have the name arg be from some enum
                            playerLobbyIntent.putExtra("gameId", gameId)
                            startActivity(playerLobbyIntent)
                        }
                        else if (isGameActive) {
                            dialogJoinGame.show(supportFragmentManager, "join game dialog")
                            //TODO add toast like pop up to explain that the game is active and cannot be joined
                            Log.e("DialogFragment", "Cannot join active game")
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

    fun signInToFirebaseAnonymously() {

        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    TODO("HANDLE FAILED SIGNIN (note: double check that gapps is installed on VM")
                }
            }
    }

    companion object {
        private const val TAG = "StartPage"
    }
}
