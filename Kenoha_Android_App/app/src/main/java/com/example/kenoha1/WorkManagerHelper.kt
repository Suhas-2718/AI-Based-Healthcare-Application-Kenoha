package com.example.kenoha1

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    fun scheduleWaterReminder(context: Context, reminder: WaterReminderActivity.Reminder) {

        val data = Data.Builder()
            .putString("fromTime", reminder.fromTime)
            .putString("toTime", reminder.toTime)
            .putString("intervalText", reminder.intervalText)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            reminder.intervalMinutes.toLong(), TimeUnit.MINUTES
        )
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_reminder_${reminder.fromTime}",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
