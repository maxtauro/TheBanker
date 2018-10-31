package com.maxtauro.monopolywallet.util

import com.google.firebase.database.FirebaseDatabase

class FirebaseNotificationUtil() {

    fun startGame(gameId: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val notifications = ref.child("notificationRequests")

        val notification = HashMap<String, String>()
        notification.put("gameId", gameId)
        notification.put("NOTIFICATION_TYPE", "START_GAME_NOTIFICATION")

        notifications.push().setValue(notification)
    }

}