package com.example.kenoha1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInRedirectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_sign_up)

        dbHelper = UserDatabaseHelper(this)

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        confirmPasswordField = findViewById(R.id.confirmPasswordField)
        signUpButton = findViewById(R.id.signUpButton)
        signInRedirectButton = findViewById(R.id.signInRedirectButton)

        // Redirect to SignIn
        signInRedirectButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Sign Up logic
        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user already exists
            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "User already exists. Please sign in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add user to SQLite
            val success = dbHelper.addUser(email, password)
            if (success) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                // Save email in SharedPreferences
                val sharedPref: SharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user_email", email)
                    apply()
                }

                // Redirect to ProfileActivity to complete profile
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
