package com.example.kenoha1

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val username = sharedPref.getString("user_name", "User") ?: "User"

        // --- Greeting Text ---
        val greetingText = findViewById<TextView>(R.id.greetingText)
        greetingText.text = "Hi $username"

        val fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in)
        greetingText.startAnimation(fadeScaleAnim)

        // --- Animate Cards ---
        val cards = listOf(
            findViewById<CardView>(R.id.quickAssistCard),
            findViewById<CardView>(R.id.emergencyCard),
            findViewById<CardView>(R.id.remindersCard),
            findViewById<CardView>(R.id.userDetailsCard),

        )
        cards.forEach { it.startAnimation(fadeScaleAnim) }

        // --- Quick Assistance ---
        findViewById<CardView>(R.id.quickAssistCard).setOnClickListener {
            startActivity(Intent(this, AssistanceActivity::class.java))
        }

        // --- Emergency Call ---
        findViewById<CardView>(R.id.emergencyCard).setOnClickListener {
            val email = sharedPref.getString("user_email", "") ?: ""
            if (email.isNotEmpty()) {
                val intent = Intent(this, EmergencyCallActivity::class.java)
                intent.putExtra("email", email) // pass email
                startActivity(intent)
            } else {
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Reminders ---
        findViewById<CardView>(R.id.remindersCard).setOnClickListener {
            startActivity(Intent(this, WaterReminderActivity::class.java))
        }

        // --- User Details Card ---
        findViewById<CardView>(R.id.userDetailsCard).setOnClickListener {
            val email = sharedPref.getString("user_email", "") ?: ""
            if (email.isNotEmpty()) {
                val intent = Intent(this, UserDetailsActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
