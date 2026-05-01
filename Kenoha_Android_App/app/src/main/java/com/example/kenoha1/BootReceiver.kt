package com.example.kenoha1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(context, "Device restarted. Rescheduling reminders...", Toast.LENGTH_SHORT).show()

            // Reload saved reminders from SharedPreferences
            val sharedPreferences = context.getSharedPreferences("WaterReminders", Context.MODE_PRIVATE)
            val gson = com.google.gson.Gson()
            val json = sharedPreferences.getString("reminders", null)
            if (json != null) {
                val type = com.google.gson.reflect.TypeToken.getParameterized(MutableList::class.java, WaterReminderActivity.Reminder::class.java).type
                val reminders: MutableList<WaterReminderActivity.Reminder> = gson.fromJson(json, type)
                reminders.forEach { WorkManagerHelper.scheduleWaterReminder(context, it) }
            }
        }
    }
}
