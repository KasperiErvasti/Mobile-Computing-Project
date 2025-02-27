package com.example.mobile_computing_project.sensor

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.mobile_computing_project.MainActivity
import android.Manifest
import com.example.mobile_computing_project.R

class NotificationHelper(private val activity: Activity) {
    private val CHANNEL_ID = "Main Channel ID"
    private val NOTIFICATION_ID = 1


    fun requestNotificationPermissions(launcher: ActivityResultLauncher<String>): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return true
        } else {
            createNotification("Notifications enabled", "Notifications have already been enabled")
            return false
        }
    }

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Main Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Main notification channel"
                setShowBadge(true)
            }

            getSystemService(activity, NotificationManager::class.java)
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(activity, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        textTitle: String,
        textContent: String
    ) {
        val notificationManager =
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an explicit intent for an Activity in your app.
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val builder = NotificationCompat.Builder(activity, CHANNEL_ID)
            .setSmallIcon(R.drawable.speed_notification_icon)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, builder)
    }

}
