package com.maxtauro.monopolywallet.util

import com.google.firebase.database.FirebaseDatabase

class FirebaseNotificationUtil() {

    fun sendNotificationToUser(gameId: String, message: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val notifications = ref.child("notificationRequests")

        val notification = HashMap<String, String>()
        notification.put("username", gameId)
        notification.put("message", message)

        notifications.push().setValue(notification)
    }

}