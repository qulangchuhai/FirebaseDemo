package com.cn.firebasedemo

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyService : FirebaseMessagingService() {
    companion object {
        const val CHANNEL_ID = "channel id"
        const val CHANNEL_NAME = "test"
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "new token $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotify(message)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.e(TAG, "onDeletedMessages ")
    }

    private fun showNotify(message: RemoteMessage) {
        Log.e(TAG, "Title :${message.notification?.title.orEmpty()}")
        Log.e(TAG, "Body :${message.notification?.body}")


        val manager =
            getContext().getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val builder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val channel: NotificationChannel =
                manager.getNotificationChannel(CHANNEL_ID)
            builder = Notification.Builder(getContext(), CHANNEL_ID)
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.lightColor = -0x100
            channel.vibrationPattern = longArrayOf(0, 100, 300)
        } else {
            builder = Notification.Builder(getContext())
            builder.setDefaults(Notification.DEFAULT_SOUND)
            builder.setLights(-0x100, 0, 2000)
            builder.setVibrate(longArrayOf(0, 100, 300))
        }
        builder.setLargeIcon(
            BitmapFactory.decodeResource(getContext().resources, R.drawable.large_icon)
        )
        builder.setSmallIcon(R.drawable.ic_normal)
        builder.setContentTitle(message.notification?.title.orEmpty())
        builder.setTicker("Ticker")
        builder.setContentText(message.notification?.body.orEmpty())
        builder.setWhen(System.currentTimeMillis())

        builder.setAutoCancel(true)
//        builder.setContentIntent(contentIntent)
        val baseNF = builder.build()

        manager.notify(1, baseNF)
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val notificationManager =
            FirebaseApp.getInstance().applicationContext
                .getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.setShowBadge(true)
            notificationManager?.createNotificationChannel(channel)
    }

    private fun getContext(): Context {
        return FirebaseApp.getInstance().applicationContext
    }
}