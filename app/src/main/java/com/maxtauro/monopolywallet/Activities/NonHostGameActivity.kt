package com.maxtauro.monopolywallet.Activities

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import com.maxtauro.monopolywallet.R
import kotlinx.android.synthetic.main.activity_host_game.*
import kotlinx.android.synthetic.main.app_bar_host_game.*

/**
 * Activity for the Game from the Non-Host user
 */
class NonHostGameActivity :  GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }
}