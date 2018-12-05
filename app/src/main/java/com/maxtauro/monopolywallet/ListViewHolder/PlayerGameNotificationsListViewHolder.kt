package com.maxtauro.monopolywallet.ListViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.NotificationTypes.PlayerGameNotification
import com.maxtauro.monopolywallet.util.PlayerGameNotificationUtil

class PlayerGameNotificationsListViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    lateinit var txt_player_id: TextView
    lateinit var txt_amount: TextView
    lateinit var txt_notification_type: TextView

    lateinit var playerGameNotifications: PlayerGameNotification

    constructor(itemView : View) : super(itemView) {

        txt_player_id = itemView.findViewById(R.id.txt_notification_player_id)
        txt_amount = itemView.findViewById(R.id.txt_notification_amount)
        txt_notification_type = itemView.findViewById(R.id.txt_notification_type)

        itemView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {}
}