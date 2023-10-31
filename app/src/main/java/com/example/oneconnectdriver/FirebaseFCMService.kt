package com.example.oneconnectdriver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseFCMService : FirebaseMessagingService(){
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder = NotificationCompat.Builder(this, "DRIVER CHANNEL ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSound(Uri.parse(message.notification?.sound))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(notifyPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "DRIVER CHANNEL NAME"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("DRIVER CHANNEL ID", name, importance)
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(
            0,
            builder.build()
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}