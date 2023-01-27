package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(applicationContext: Context, description: String, status: Boolean) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    contentIntent.putExtra("description", description)
    contentIntent.putExtra("status", status)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val buttonPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            contentIntent,
            FLAGS)


    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_notification_channel_id)
    )
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(applicationContext
                    .getString(R.string.notification_description))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_done_24)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
    if(status)
        builder.setSmallIcon(R.drawable.ic_baseline_done_24)
    else
        builder.setSmallIcon(R.drawable.ic_baseline_error_outline_24)

    notify(NOTIFICATION_ID, builder.build())
}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}
