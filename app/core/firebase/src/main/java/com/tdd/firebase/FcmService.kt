package com.tdd.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tdd.design_system.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Timber.d("[FCM] FcmService -> token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Timber.d("[FCM] FcmService -> notification: ${message.notification} data: ${message.data}")

        sendNotification(
            title = message.notification?.title.orEmpty(),
            body = message.notification?.body.orEmpty(),
            type = message.data[TYPE].orEmpty(),
            imageUrl = message.data[IMAGE_URL].orEmpty()
        )
    }

    private fun sendNotification(title: String, body: String, type: String, imageUrl: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentText(body)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .build()

        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_logo)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine(body)
                    .setSummaryText("${notificationManager.activeNotifications.size}")
            )
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        notificationManager.notify(SUMMARY_ID, summaryNotification)
    }

    companion object {
        private const val CHANNEL_ID: String = "BookShelf"
        private const val GROUP_KEY: String = "BookShelf_GROUP"
        private const val TITLE: String = "title"
        private const val BODY: String = "body"
        private const val TYPE: String = "type"
        private const val IMAGE_URL: String = "imageUrl"
        private const val SUMMARY_ID = 0
    }
}