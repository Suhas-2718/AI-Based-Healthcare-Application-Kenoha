package com.example.kenoha1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpRedirectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        dbHelper = UserDatabaseHelper(this)

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        signInButton = findViewById(R.id.signInButton)
        signUpRedirectButton = findViewById(R.id.signUpRedirectButton)

        // Redirect to SignUp
        signUpRedirectButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        // Sign In logic
        signInButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.validateUser(email, password)) {
                // Save logged-in user info in SharedPreferences
                val sharedPref: SharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user_email", email)
                    putString("user_name", dbHelper.getUserName(email)) // helper function to get name
                    apply()
                }

                // Redirect to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
