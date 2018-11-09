package com.maxtauro.monopolywallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.IntentExtrasConstants

/**
 *  Activity for the Game from the Host user
 */
class HostGame :  AppCompatActivity() {

    lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_game)

    }

    override fun onStart() {
        super.onStart()

        val gameId: String = intent.extras[IntentExtrasConstants.GAME_ID_EXTRA] as String //TODO, pass whole game dao in extras
        firebaseHelper = FirebaseHelper(gameId)
    }


}