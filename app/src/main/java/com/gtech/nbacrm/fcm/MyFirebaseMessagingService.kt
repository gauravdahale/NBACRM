package com.gtech.nbacrm.fcm;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gtech.nbacrm.MainActivity
import com.gtech.nbacrm.R

/**
 * Created by Gaurav on 8/8/2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var notificationTitle: String? = null; var notificationBody:kotlin.String? = null
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendNotification(
            messageTitle: String,
            messageBody: String
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }




    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)
        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            /*
            if (*/
/* Check if data needs to be processed by long running job */ /* true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }*/
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            notificationTitle = remoteMessage.getNotification()!!.getTitle();
            notificationBody = remoteMessage.getNotification()!!.getBody();
        sendNotification(notificationTitle!!,notificationBody!!)
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
// message, here is where that should be initiated. See sendNotification method below.
    }
    private fun sendRegistrationToServer(token: Comparable<String>) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
    if(uid!=null) {
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("tokens").push()
            .setValue(
                token as String
            )
        //Creating a movie object with user defined variables
        //Creating a movie object with user defined variables
        var prefs: SharedPreferences? = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs!!.edit()
        editor.putString("USER_TOKEN", token)
        editor.apply()
    }




    }
    companion object {
        private const val TAG = "FIREBASEMESSAGINSERVICE"
    }
}