package com.maxtauro.monopolywallet.util

import com.google.firebase.database.FirebaseDatabase

/**
 *  A util class for building paths for in firebase real-time database
 */
class FirebaseReferenceUtil: FirebaseReferenceConstants() {

    // Database
    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference

    fun getHostRef(): String {

        val referenceBuilder = ReferenceBuilder()

        referenceBuilder.addNodePath(GAME_INFO_NODE_KEY)
        referenceBuilder.addNodePath(HOST_NAME_NODE_KEY)

        return referenceBuilder.build()
    }

    fun getGameActivePath(): String {

        val referenceBuilder = ReferenceBuilder()

        referenceBuilder.addNodePath(GAME_INFO_NODE_KEY)
        referenceBuilder.addNodePath(GAME_ACTIVE_NODE_KEY)

        return referenceBuilder.build()
    }


    class ReferenceBuilder {

        var reference: String = ""

        fun addNodePath(nodePath: String) {
            reference += "$nodePath/"
        }

        fun build() = reference.trimEnd('/')

    }
}