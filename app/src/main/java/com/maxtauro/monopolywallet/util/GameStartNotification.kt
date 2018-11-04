package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.util.NotificationTypes.NotificationBase
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

/**
 * TODO add authoring, date, and desc
 */

//TODO maybe make interfaec?
class GameStartNotification(gameId : String): NotificationBase(gameId, StandardNotifications.START_GAME_NOTIFICATION) {

    enum class MessageDataFields(val dataFieldId: String) {
        GAME_ID("gameId")
    }
}