package com.maxtauro.monopolywallet

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.maxtauro.monopolywallet.util.NotificationTypes.HostNotification

class HostNotificationListViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    lateinit var txt_player_id: TextView
    lateinit var txt_amount: TextView
    lateinit var txt_notification_type: TextView

    lateinit var hostNotification: HostNotification

    constructor(itemView : View) : super(itemView) {

        txt_player_id = itemView.findViewById(R.id.txt_notification_player_id)
        txt_amount = itemView.findViewById(R.id.txt_notification_amount)
        txt_notification_type = itemView.findViewById(R.id.txt_notification_type)

        itemView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Log.d("HostNotificationList", "Click on notification")
    }
}