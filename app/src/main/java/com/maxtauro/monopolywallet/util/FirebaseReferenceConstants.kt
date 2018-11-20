package com.maxtauro.monopolywallet.util

/**
 * Constants for firebase paths
 */

open class FirebaseReferenceConstants {


    // These constants are placed in a companion method in order to access them as static values
    companion object {

        const val PLAYER_LIST_NODE_KEY = "playerList"
        const val PLAYER_BALANCE_NODE_KEY = "playerBalance"
        const val GAME_INFO_NODE_KEY = "gameInfo"

        const val GAME_ACTIVE_NODE_KEY = "gameActive"
        const val HOST_NAME_NODE_KEY = "hostName"
        const val HOST_NOTIFICATION_LIST_KEY = "hostNotificationList"
    }


}