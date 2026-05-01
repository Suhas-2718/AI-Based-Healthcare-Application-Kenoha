package com.example.kenoha1

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, "water_channel")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Time to Hydrate 💧")
            .setContentText("Drink a glass of water and stay refreshed!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        // ✅ Check if notification permission is granted before showing notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } else {
            // (Optional) Handle denied permission — e.g. log or silently skip
        }
    }
}
