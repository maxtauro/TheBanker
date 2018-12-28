package com.maxtauro.monopolywallet.ListViewHolder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.NotificationTypes.StandardNotifications

class PlayerGameNotificationsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val txt_player_name: TextView = itemView.findViewById(R.id.txt_notification_player_id)
    private val txt_amount: TextView = itemView.findViewById(R.id.txt_notification_amount)
    private val txt_notification_type: TextView = itemView.findViewById(R.id.txt_notification_type)

    lateinit var playerGameNotifications: PlayerGameNotification

    override fun onClick(v: View?) {}

    fun displayNotification(notification: PlayerGameNotification, context: Context) {
        playerGameNotifications = notification

        val fromString = context.getString(R.string.notification_from_msg)
        val amountString = context.getString(R.string.notification_amount_msg)

        val notificationType = notification.notificationType

        val notificationTypeString =
                when (notificationType) {
                    StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION -> context.getString(R.string.notification_type_bank_credit_msg)
                    StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION -> context.getString(R.string.notification_type_bank_debit_msg)
                    StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST -> context.getString(R.string.notification_type_player_transaction_send_msg)
                    StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST -> context.getString(R.string.notification_type_player_transaction_request_msg)
                    else -> TODO("Implement Error Handling for bad notification")
                }

        txt_player_name.text = fromString.format(notification.playerName)
        txt_notification_type.text = notificationTypeString
        txt_amount.text = amountString.format(notification.amount)
    }
}