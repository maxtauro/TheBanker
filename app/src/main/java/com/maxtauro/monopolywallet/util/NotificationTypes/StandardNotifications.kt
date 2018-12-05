package com.maxtauro.monopolywallet.util.NotificationTypes


/**
 * Enum class of standard notification types for the application
 */

enum class StandardNotifications {

    //Game Notifications
    START_GAME_NOTIFICATION,
    END_GAME_NOTIFICATION,
    LEAVE_GAME,

    //Bank Transaction Notifications
    BANK_DEBIT_TRANSACTION_NOTIFICATION,
    BANK_CREDIT_TRANSACTION_NOTIFICATION,
    BANK_CREDIT_CONFIRMATION,
    BANK_DEBIT_CONFIRMATION,

    //P2P Transaction Notification
    PLAYER_SEND_TRANSACTION_REQUEST,
    PLAYER_REQUEST_TRANSACTION_REQUEST,

    //Generic notification, used when notification type does not matter
    GENERIC_NOTIFICATION

}