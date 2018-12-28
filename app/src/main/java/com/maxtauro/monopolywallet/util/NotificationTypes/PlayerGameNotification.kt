package com.maxtauro.monopolywallet.util.NotificationTypes

open class PlayerGameNotification(val notificationKey: String, val playerId: String, val playerName: String, val amount: Int, gameId: String, notificationType: StandardNotifications):
        NotificationBase(gameId, notificationType) {

    // Need a no argument ctor for firebase, you should NEVER call this explicitly
    constructor() : this("", "", "", 0, "", StandardNotifications.GENERIC_NOTIFICATION) {}

     enum class MessageDataFields(val dataFieldId: String) {
        GAME_ID("gameId"),
        PLAYER_ID("playerId"),
         PLAYER_NAME("playerName"),
        AMOUNT("paymentAmount"),
        WINNER_ID("winnerId")
     }
}