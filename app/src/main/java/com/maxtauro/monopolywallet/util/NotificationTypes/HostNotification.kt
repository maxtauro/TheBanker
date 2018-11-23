package com.maxtauro.monopolywallet.util.NotificationTypes

open class HostNotification(val notificationKey: String, val playerId: String, val amount: Int, gameId: String, notificationType: StandardNotifications):
        NotificationBase(gameId, notificationType) {



    // Need a no argument ctor for firebase, you should NEVER call this explicitly
    constructor() : this("", "", 0, "", StandardNotifications.GENERIC_NOTIFICATION) {}

    public enum class MessageDataFields(val dataFieldId: String) {
        GAME_ID("gameId"),
        PLAYER_ID("playerId"),
        AMOUNT("paymentAmount")
    }
}