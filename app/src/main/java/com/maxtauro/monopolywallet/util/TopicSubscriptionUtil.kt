package com.maxtauro.monopolywallet.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * TODO add authoring, date, and desc
 */

class TopicSubscriptionUtil {


    fun subscribeToTopic(topic: String) {
        // Subscribe To Game Notifications
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = "Subscribed to $topic topic"
                    if (!task.isSuccessful) {
                        msg = "Failed to subscribe to $topic topic"
                    }
                    Log.d("JoinLobby" , msg)
                }
    }

}