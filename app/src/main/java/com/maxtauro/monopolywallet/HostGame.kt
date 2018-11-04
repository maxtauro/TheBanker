package com.maxtauro.monopolywallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.maxtauro.monopolywallet.util.FirebaseHelper

/**
 * TODO add authoring, date, and desc
 */
class HostGame :  AppCompatActivity() {

    lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_game)

    }

    override fun onStart() {
        super.onStart()

        val gameId: String = intent.extras["gameId"] as String //TODO, put this string in an enum, also pass whole game dao in extras
        firebaseHelper = FirebaseHelper(gameId)
    }


}