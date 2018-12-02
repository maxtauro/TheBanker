package com.maxtauro.monopolywallet.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maxtauro.monopolywallet.HostGame
import com.maxtauro.monopolywallet.JoinGame
import com.maxtauro.monopolywallet.R
import com.maxtauro.monopolywallet.util.BankTransactionEnums
import com.maxtauro.monopolywallet.util.FirebaseHelper
import com.maxtauro.monopolywallet.util.NotificationTypes.*
import com.maxtauro.monopolywallet.util.PlayerTransactionEnum

/**
 * TODO add authoring, date, and desc
 */
class NotificationService : FirebaseMessagingService() {

    //Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        auth = FirebaseAuth.getInstance()
        super.onCreate()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.d(TAG, "From: ${remoteMessage?.from}")

        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            when (it.body) {
                StandardNotifications.START_GAME_NOTIFICATION.toString() -> startGame(remoteMessage)
                StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION.toString() -> processBankTransactionRequest(remoteMessage, BankTransactionEnums.DEBIT)
                StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION.toString() -> processBankTransactionRequest(remoteMessage, BankTransactionEnums.CREDIT)
                StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST.toString() -> processPlayerTransactionRequest(remoteMessage, PlayerTransactionEnum.REQUEST_MONEY)
                StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST.toString() -> processPlayerTransactionRequest(remoteMessage, PlayerTransactionEnum.SEND_MONEY)
            }
        }

    }

    private fun startGame(remoteMessage: RemoteMessage?) {

        val gameIdField = GameStartNotification.MessageDataFields.GAME_ID.toString()
        val gameId = remoteMessage!!.data[gameIdField]

        if (gameId == null || gameId == "") TODO("HANDLE BAD NOTIFICATION FOR START GAME")

        val gameHostId : String = FirebaseHelper.getGameHostUid(gameId!!)

        val startGameIntent: Intent

        startGameIntent = if (gameHostId == auth.uid) {
            Intent(this, HostGame::class.java)
        }

        else {
            Intent(this, JoinGame::class.java)
        }

        sendNotification(remoteMessage.notification!!.body!!)

        startGameIntent.putExtra("gameId", gameId)
        startGameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(startGameIntent)
    }

    private fun processBankTransactionRequest(remoteMessage: RemoteMessage, creditDebit: BankTransactionEnums) {

        val gameIdField = PlayerGameNotification.MessageDataFields.GAME_ID.toString()

        val payerIdField = PlayerGameNotification.MessageDataFields.PLAYER_ID.toString()
        val paymentAmountField = BankTransactionRequestNotification.MessageDataFields.PAYMENT_AMOUNT.toString()

        val gameId = remoteMessage.data[gameIdField]
        val payerId = remoteMessage.data[payerIdField]
        val paymentAmount = remoteMessage.data[paymentAmountField]?.toInt()

        if (gameId == null || gameId == "" || payerId == null || payerId == "" || paymentAmount == null) TODO("HANDLE BAD NOTIFICATION FOR Payment")

        val notificationType: StandardNotifications = //TODO, can we just pass this as a parameter?
                if (creditDebit == BankTransactionEnums.DEBIT) StandardNotifications.BANK_DEBIT_TRANSACTION_NOTIFICATION
                else StandardNotifications.BANK_CREDIT_TRANSACTION_NOTIFICATION

        val paymentIntentNotification = BankTransactionRequestNotification(gameId, payerId, paymentAmount, notificationType)
        val firebaseHelper = FirebaseHelper(gameId)
        val hostUid = FirebaseHelper.getGameHostUid(gameId)

        //If the current user is the host, the bank transaction does not need to be approved, so it is processed right away
        if (auth.uid == hostUid) firebaseHelper.processBankPayment(paymentIntentNotification, creditDebit)
        else firebaseHelper.createPayBankIntentRequest(paymentIntentNotification, creditDebit)
        //TODO toast and icon to indicate there is a payment awaiting approval
    }

    private fun processPlayerTransactionRequest(remoteMessage: RemoteMessage, playerTransactionType: PlayerTransactionEnum) {

//        val gameIdField = PlayerGameNotification.MessageDataFields.GAME_ID.toString()
//
//        val senderIdField = PlayerGameNotification.MessageDataFields.PLAYER_ID.toString()
//        val paymentAmountField = BankTransactionRequestNotification.MessageDataFields.PAYMENT_AMOUNT.toString()
//
//        val gameId = remoteMessage.data[gameIdField]
//        val senderId = remoteMessage.data[senderIdField]
//        val paymentAmount = remoteMessage.data[paymentAmountField]?.toInt()
//
//        if (gameId == null || gameId == "" || senderId == null || senderId == "" || paymentAmount == null) TODO("HANDLE BAD NOTIFICATION FOR Payment")
//
//        val notificationType: StandardNotifications =
//                if (playerTransactionType == PlayerTransactionEnum.SEND_MONEY) StandardNotifications.PLAYER_SEND_TRANSACTION_REQUEST
//                else StandardNotifications.PLAYER_REQUEST_TRANSACTION_REQUEST
//
//        val playerTransactionRequestNotification = PlayerTransactionRequestNotification(auth.uid!!, gameId, senderId, paymentAmount, notificationType)
//        val firebaseHelper = FirebaseHelper(gameId)
//
//        firebaseHelper.createPlayerTransactionRequest(playerTransactionRequestNotification)
        //TODO toast and icon to indicate there is a transaction awaiting approval
    }


    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
//        // [START dispatch_job]
//        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//        val myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService::class.java)
//                .setTag("my-job-tag")
//                .build()
//        dispatcher.schedule(myJob)
//        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {


//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.templogo)
                .setContentTitle(getString(R.string.fcm_message))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
//
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "NotificationService"
    }
}
