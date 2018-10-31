package com.maxtauro.monopolywallet.util

import com.maxtauro.monopolywallet.util.NotificationTypes.NotificationBase
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class GameStartNotification(gameId : String): NotificationBase(gameId, StandardNotifications.START_GAME_NOTIFICATION) {

}