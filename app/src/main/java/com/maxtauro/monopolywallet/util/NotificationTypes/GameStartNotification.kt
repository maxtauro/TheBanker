package com.maxtauro.monopolywallet.util.NotificationTypes


/**
 * Notification for starting the game
 */

class GameStartNotification(gameId : String): NotificationBase(gameId, StandardNotifications.START_GAME_NOTIFICATION) {

    enum class MessageDataFields(val dataFieldId: String) {
        GAME_ID("gameId")
    }
}