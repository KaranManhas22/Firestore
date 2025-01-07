package com.karan.firestore

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Build.VERSION_CODES.N
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.media3.common.util.Log
import com.google.api.Context
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Firebase_Cloud_Messaging:FirebaseMessagingService() {
var firebase=""
    val notificationManager:NotificationManager by lazy {
        getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationData= message.notification
        firebase= message.data.toString()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name=getString(R.string.app_name)
            val descriptionText=getString(R.string.channel_description)
            val importance=NotificationManager.IMPORTANCE_DEFAULT
            val channelID=getString(R.string.default_notification_channel_id)
            val channel = NotificationChannel(channelID,name,importance).apply {
                description=descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
        generateNotification(message)

    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

    }

    private fun generateNotification(notificationData: RemoteMessage) {
        var intent=Intent(this,MainActivity::class.java)
        intent.putExtra("data",firebase)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pandingIntent=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder= NotificationCompat.Builder(this,resources.getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationData?.notification?.title?:"")
            .setContentText(notificationData?.notification?.body?:"")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setContentIntent(pandingIntent)
        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(),builder.build())


    }
}