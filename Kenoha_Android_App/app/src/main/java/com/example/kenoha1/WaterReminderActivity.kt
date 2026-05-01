package com.example.kenoha1

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WaterReminderActivity : AppCompatActivity() {

    private lateinit var switchReminder: Switch
    private lateinit var fromTimeText: TextView
    private lateinit var toTimeText: TextView
    private lateinit var intervalGroup: RadioGroup
    private lateinit var saveReminderButton: Button
    private lateinit var savedRemindersList: LinearLayout
    private lateinit var inputSection: LinearLayout

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private val gson = Gson()
    private val reminders = mutableListOf<Reminder>()

    data class Reminder(
        val fromTime: String,
        val toTime: String,
        val intervalText: String,
        val intervalMinutes: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_reminder)

        // --- Initialize Views ---
        sharedPreferences = getSharedPreferences("WaterReminders", Context.MODE_PRIVATE)
        switchReminder = findViewById(R.id.switchReminder)
        fromTimeText = findViewById(R.id.fromTimeText)
        toTimeText = findViewById(R.id.toTimeText)
        intervalGroup = findViewById(R.id.intervalGroup)
        saveReminderButton = findViewById(R.id.saveReminderButton)
        savedRemindersList = findViewById(R.id.savedRemindersList)
        inputSection = findViewById(R.id.inputSection)

        loadRemindersFromPreferences()
        animateTitle()

        // --- Toggle logic ---
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            inputSection.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE

            if (!isChecked) {
                // Cancel all scheduled reminders
                reminders.forEach { reminder ->
                    androidx.work.WorkManager.getInstance(this)
                        .cancelUniqueWork("water_reminder_${reminder.fromTime}")
                }
                Toast.makeText(this, "💧 Water reminders stopped", Toast.LENGTH_SHORT).show()
            }
        }

        fromTimeText.setOnClickListener { showTimePicker(true) }
        toTimeText.setOnClickListener { showTimePicker(false) }

        intervalGroup.setOnCheckedChangeListener { _, _ -> checkSaveVisibility() }
        saveReminderButton.setOnClickListener { saveReminder() }
    }

    private fun animateTitle() {
        val titleText = findViewById<TextView>(R.id.titleText)
        titleText.alpha = 0f
        titleText.translationY = 50f
        titleText.animate().alpha(1f).translationY(0f).setDuration(700).start()
    }

    private fun showTimePicker(isFrom: Boolean) {
        val cal = java.util.Calendar.getInstance()
        val picker = android.app.TimePickerDialog(
            this,
            { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                if (isFrom) fromTimeText.text = time else toTimeText.text = time
                checkSaveVisibility()
            },
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE),
            true
        )
        picker.show()
    }

    private fun checkSaveVisibility() {
        val hasFrom = fromTimeText.text != "Select Time"
        val hasTo = toTimeText.text != "Select Time"
        val hasInterval = intervalGroup.checkedRadioButtonId != -1
        saveReminderButton.visibility = if (hasFrom && hasTo && hasInterval) Button.VISIBLE else Button.GONE
    }

    private fun getIntervalMinutes(text: String): Int {
        return when {
            text.contains("2 minutes") -> 2
            text.contains("5 minutes") -> 5
            text.contains("15 minutes") -> 15
            text.contains("30 minutes") -> 30
            text.contains("1 hour") -> 60
            text.contains("1.5 hours") -> 90
            text.contains("2 hours") -> 120
            else -> 15
        }
    }

    private fun saveReminder() {
        val selectedId = intervalGroup.checkedRadioButtonId
        if (selectedId == -1) return

        val intervalText = findViewById<RadioButton>(selectedId).text.toString()
        val intervalMinutes = getIntervalMinutes(intervalText)

        val reminder = Reminder(
            fromTime = fromTimeText.text.toString(),
            toTime = toTimeText.text.toString(),
            intervalText = intervalText,
            intervalMinutes = intervalMinutes
        )

        reminders.add(reminder)
        saveRemindersToPreferences()
        displaySavedReminders()
        scheduleReminder(reminder)

        Toast.makeText(this, "💧 Reminder Saved!", Toast.LENGTH_SHORT).show()
        saveReminderButton.visibility = Button.GONE
    }

    private fun saveRemindersToPreferences() {
        val editor = sharedPreferences.edit()
        editor.putString("reminders", gson.toJson(reminders))
        editor.apply()
    }

    private fun loadRemindersFromPreferences() {
        val json = sharedPreferences.getString("reminders", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Reminder>>() {}.type
            reminders.clear()
            reminders.addAll(gson.fromJson(json, type))
            displaySavedReminders()
            // Reschedule all reminders
            reminders.forEach { scheduleReminder(it) }
        }
    }

    private fun displaySavedReminders() {
        savedRemindersList.removeAllViews()
        for ((index, reminder) in reminders.withIndex()) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(30, 35, 30, 35)
                background = getDrawable(R.drawable.reminder_card_bg)
            }

            val text = TextView(this).apply {
                text = "From: ${reminder.fromTime}\nTo: ${reminder.toTime}\nInterval: ${reminder.intervalText}"
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.black))
            }

            card.addView(text)
            savedRemindersList.addView(card)

            card.setOnLongClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setPositiveButton("Yes") { _, _ ->
                        reminders.removeAt(index)
                        saveRemindersToPreferences()
                        displaySavedReminders()
                        // Cancel the specific WorkManager task
                        androidx.work.WorkManager.getInstance(this)
                            .cancelUniqueWork("water_reminder_${reminder.fromTime}")
                        Toast.makeText(this, "Reminder Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            }
        }
    }

    private fun scheduleReminder(reminder: Reminder) {
        WorkManagerHelper.scheduleWaterReminder(this, reminder)
    }
}
