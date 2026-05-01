package com.example.kenoha1

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class EmergencyCallActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var successIcon: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dbHelper: UserDatabaseHelper

    private val BACKEND_URL = "http://172.20.10.13:5000/call" // Replace with your backend IP
    private var currentEmail: String = ""
    private var emergencyPhone: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_call)

        progressBar = findViewById(R.id.loadingProgress)
        statusText = findViewById(R.id.statusText)
        successIcon = findViewById(R.id.successIcon)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dbHelper = UserDatabaseHelper(this)

        // Get email from Intent
        currentEmail = intent.getStringExtra("email") ?: ""

        if (currentEmail.isEmpty()) {
            Toast.makeText(this, "User email not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch user details from
        fetchUserDetailsFromDB()

        // Start emergency process
        startEmergencyProcess()
    }

    private fun fetchUserDetailsFromDB() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT name, phone FROM users WHERE email = ?",
            arrayOf(currentEmail)
        )
        if (cursor.moveToFirst()) {
            userName = cursor.getString(0)
            emergencyPhone = cursor.getString(1)
        }
        cursor.close()
        db.close()

        if (emergencyPhone.isNullOrEmpty()) {
            Toast.makeText(this, "Emergency number not set for this user", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startEmergencyProcess() {
        progressBar.visibility = View.VISIBLE
        statusText.text = "📍 Getting location..."

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getLocationAndSend()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                getLocationAndSend()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied. Sending alert without location.",
                    Toast.LENGTH_LONG
                ).show()
                sendToBackend(null)
            }
        }

    private fun getLocationAndSend() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                sendToBackend(location)
            }.addOnFailureListener {
                Toast.makeText(this, "Error fetching location. Sending without it.", Toast.LENGTH_SHORT).show()
                sendToBackend(null)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            sendToBackend(null)
        }
    }

    private fun sendToBackend(location: Location?) {
        statusText.text = "🚨 Sending emergency alert..."

        val jsonBody = JSONObject().apply {
            put("name", userName)
            put("number", emergencyPhone)
            location?.let {
                put("latitude", it.latitude)
                put("longitude", it.longitude)
            }
        }

        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(
            Request.Method.POST, BACKEND_URL,
            { _ ->
                progressBar.visibility = View.GONE
                successIcon.visibility = View.VISIBLE
                statusText.text = "✅ Emergency Call Initiated Successfully!"
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 4000)
            },
            { error ->
                progressBar.visibility = View.GONE
                statusText.text = "✅ Emergency Call Initiated Successfully!"
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 3000)
            }
        ) {
            override fun getBody(): ByteArray = jsonBody.toString().toByteArray(Charsets.UTF_8)
            override fun getBodyContentType(): String = "application/json"
        }

        queue.add(request)
    }
}
