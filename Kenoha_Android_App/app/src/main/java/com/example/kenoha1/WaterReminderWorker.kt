package com.example.kenoha1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class WaterReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val fromTime = inputData.getString("fromTime") ?: ""
        val toTime = inputData.getString("toTime") ?: ""
        val intervalText = inputData.getString("intervalText") ?: ""

        showNotification("💧 Time to drink water! ", "Interval: $intervalText")

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "water_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Water Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Reminders to drink water regularly" }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_water) // your water icon
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
