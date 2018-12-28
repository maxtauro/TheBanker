package com.maxtauro.monopolywallet.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.maxtauro.monopolywallet.Constants.FirebaseReferenceConstants
import java.lang.NullPointerException

/**
 *  A util class for building paths and database references for firebase real-time database
 */
class FirebaseReferenceUtil(val gameId: String): FirebaseReferenceConstants() {

    // Database
    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference

    private val referenceBuilder = ReferenceBuilder(gameId, databaseRef)

    fun getHostPath(): String {

        referenceBuilder.addNodePath(GAME_INFO_NODE_KEY)
        referenceBuilder.addNodePath(HOST_NAME_NODE_KEY)

        return referenceBuilder.buildPath()
    }

    fun getGameActivePath(): String {

        referenceBuilder.addNodePath(GAME_INFO_NODE_KEY)
        referenceBuilder.addNodePath(GAME_ACTIVE_NODE_KEY)

        return referenceBuilder.buildPath()
    }

    fun getPlayerBalanceRef(playerId: String?): DatabaseReference {

        if (playerId == null) {
            throw NullPointerException("Could not get player ID") //TODO implement an error handling framework
        }

        referenceBuilder.addNodePath(PLAYER_LIST_NODE_KEY)
        referenceBuilder.addNodePath(playerId)
        referenceBuilder.addNodePath(PLAYER_BALANCE_NODE_KEY)

        return referenceBuilder.buildRef()
    }

    fun getPlayerBalancePath(playerId: String): String {

        if (playerId == null) {
            throw NullPointerException("Could not get player ID") //TODO implement an error handling framework
        }

        referenceBuilder.addNodePath(PLAYER_LIST_NODE_KEY)
        referenceBuilder.addNodePath(playerId)
        referenceBuilder.addNodePath(PLAYER_BALANCE_NODE_KEY)

        return referenceBuilder.buildPath()
    }

    fun getPlayerNotificationPath(playerId: String): String {
        referenceBuilder.addNodePath(PLAYER_LIST_NODE_KEY)
        referenceBuilder.addNodePath(playerId)
        referenceBuilder.addNodePath(PLAYER_NOTIFICATION_LIST_KEY)

        return referenceBuilder.buildPath()
    }

    fun getPlayerNotificationRef(playerId: String): DatabaseReference {
        referenceBuilder.addNodePath(PLAYER_LIST_NODE_KEY)
        referenceBuilder.addNodePath(playerId)
        referenceBuilder.addNodePath(PLAYER_NOTIFICATION_LIST_KEY)

        return referenceBuilder.buildRef()
    }

    fun getPlayerListRef(): DatabaseReference {
        referenceBuilder.addNodePath(PLAYER_LIST_NODE_KEY)
        return referenceBuilder.buildRef()
    }


    private class ReferenceBuilder(val gameId: String, val databaseReference: DatabaseReference) {

        var reference: String = "$gameId/"

        fun addNodePath(nodePath: String) {
            reference += "$nodePath/"
        }

        fun buildPath(): String {
            val resultRef = reference
            reference = "$gameId/"
            return resultRef
        }

        fun buildRef(): DatabaseReference {
            return databaseReference.child(buildPath())
        }

    }
}