package com.example.equityspin

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.equityspin.R  // Asegúrate de tener este import para los recursos


// A helper class to manage status bar notification in the app
class NotificationHelper(private val context: Context) {
    companion object {
        // Constants for the standard notification channel
        const val STANDARD_CHANNEL_ID = "standard_channel"
        const val STANDARD_CHANNEL_NAME = "Standard Notifications"
        const val STANDARD_CHANNEL_DESCRIPTION = "Channel for standard status bar notifications"

        // Constants for the high-priority (heads-up) notification channel
        const val HEADS_UP_CHANNEL_ID = "heads_up_channel"
        const val HEADS_UP_CHANNEL_NAME = "Heads-up Notifications"
        const val HEADS_UP_CHANNEL_DESCRIPTION = "Channel for heads-up notifications"

        // ID used to identify notification (can be changed if multiple notification needed)
        const val NOTIFICATION_ID = 1
    }

    init {
        // Create notification channels as soon as the helper is INITIALIZED.
        createNotificationChannels()
    }

    // Create notifications channels
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Standard notification channel
            val standardChannel = NotificationChannel(
                STANDARD_CHANNEL_ID,
                STANDARD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // Medium importance
            ).apply {
                description = STANDARD_CHANNEL_DESCRIPTION
            }

            // Heads up notification channels
            val headsUpChannel = NotificationChannel(
                HEADS_UP_CHANNEL_ID,
                HEADS_UP_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // High importance
            ).apply {
                description = HEADS_UP_CHANNEL_DESCRIPTION
            }

            // Register the channels with the system NotificationManager
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(standardChannel)
            notificationManager.createNotificationChannel(headsUpChannel)
        }
    }
    // Show a standard notification in the status bar
    @SuppressLint("MissingPermission") // Assume permission is handled by the app
    fun statusBarNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, STANDARD_CHANNEL_ID)
            .setSmallIcon(R.drawable.equityspinlogo) // app logo
            .setContentTitle(title) // title shown in notification
            .setContentText(content) // message body of notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // default delivery behavior

        // Show notification using the ManagerCompat
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}