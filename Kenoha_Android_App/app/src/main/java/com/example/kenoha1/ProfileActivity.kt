//package com.example.kenoha1
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.animation.AccelerateDecelerateInterpolator
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//
//class ProfileActivity : AppCompatActivity() {
//
//    private val PICK_IMAGE_REQUEST = 101
//    private var imageUri: Uri? = null
//    private lateinit var profileImage: ImageView
//    private lateinit var dbHelper: UserDatabaseHelper
//    private lateinit var currentEmail: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile_centered)
//
//        dbHelper = UserDatabaseHelper(this)
//
//        val nameField = findViewById<EditText>(R.id.nameField)
//        val phoneField = findViewById<EditText>(R.id.phoneField)
//        val phoneField2 = findViewById<EditText>(R.id.phoneField1)
//        profileImage = findViewById(R.id.profileImage)
//        val continueButton = findViewById<Button>(R.id.continueButton)
//
//        currentEmail = intent.getStringExtra("email") ?: ""
//
//        // Fade animation
//        listOf(profileImage, nameField, phoneField, continueButton).forEachIndexed { index, view ->
//            view.alpha = 0f
//            view.translationY = 50f
//            view.animate()
//                .alpha(1f).translationY(0f)
//                .setStartDelay(index * 150L)
//                .setDuration(500)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .start()
//        }
//
//        // Load profile if already exists
//        loadUserProfile(nameField)
//
//        profileImage.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, PICK_IMAGE_REQUEST)
//        }
//
//        continueButton.setOnClickListener {
//            val name = nameField.text.toString().trim()
//            var phone = phoneField.text.toString().trim()
//            if (name.isEmpty() || phone.isEmpty()) {
//                Toast.makeText(this, "Please enter name & phone number", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (!phone.startsWith("+91")) phone = "+91$phone"
//
//            dbHelper.updateProfile(currentEmail, name, phone, imageUri?.toString())
//            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
//            sharedPref.edit().putString("user_name", name).putString("user_email", currentEmail)
//                .putString("emergency_phone", phone).apply()
//
//            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//    }
//
//    private fun loadUserProfile(nameField: EditText) {
//        val cursor = dbHelper.readableDatabase.rawQuery(
//            "SELECT name, phone, image_uri FROM users WHERE email=?",
//            arrayOf(currentEmail)
//        )
//        if (cursor.moveToFirst()) {
//            nameField.setText(cursor.getString(0))
//            findViewById<EditText>(R.id.phoneField).setText(cursor.getString(1))
//            val uriStr = cursor.getString(2)
//            if (!uriStr.isNullOrEmpty()) {
//                imageUri = Uri.parse(uriStr)
//                profileImage.setImageURI(imageUri)
//            } else profileImage.setImageResource(R.drawable.ic_default_user)
//        }
//        cursor.close()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            imageUri = data.data
//            profileImage.setImageURI(imageUri)
//        }
//    }
//}

package com.example.kenoha1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 101
    private var imageUri: Uri? = null
    private lateinit var profileImage: ImageView
    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var currentEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_centered)

        dbHelper = UserDatabaseHelper(this)

        val nameField = findViewById<EditText>(R.id.nameField)
        val phoneField = findViewById<EditText>(R.id.phoneField)      // Primary
        val phoneField2 = findViewById<EditText>(R.id.phoneField1)    // ⭐ NEW: Second phone field
        profileImage = findViewById(R.id.profileImage)
        val continueButton = findViewById<Button>(R.id.continueButton)

        currentEmail = intent.getStringExtra("email") ?: ""

        // Fade animation
        // ⭐ UPDATED: Added phoneField2 to animation list
        listOf(profileImage, nameField, phoneField, phoneField2, continueButton).forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f
            view.animate()
                .alpha(1f).translationY(0f)
                .setStartDelay(index * 150L)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Load profile if already exists
        // ⭐ UPDATED: Pass both phone fields
        loadUserProfile(nameField, phoneField, phoneField2)

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        continueButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            var phone1 = phoneField.text.toString().trim()          // primary
            var phone2 = phoneField2.text.toString().trim()         // secondary (optional)

            if (name.isEmpty() || phone1.isEmpty()) {
                Toast.makeText(this, "Please enter name & primary phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add +91 prefix for primary
            if (!phone1.startsWith("+91")) phone1 = "+91$phone1"

            // ⭐ NEW: Handle second phone (only if not empty)
            var phone2ToStore = "Phone number not set"   // default text if not given
            if (phone2.isNotEmpty()) {
                if (!phone2.startsWith("+91")) phone2 = "+91$phone2"
                phone2ToStore = phone2
            }

            // ⭐ UPDATED: Pass second phone also (update your UserDatabaseHelper.updateProfile accordingly)
            dbHelper.updateProfile(currentEmail, name, phone1, phone2ToStore, imageUri?.toString())

            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            sharedPref.edit()
                .putString("user_name", name)
                .putString("user_email", currentEmail)
                .putString("emergency_phone", phone1)
                .putString("emergency_phone2", phone2ToStore)   // ⭐ NEW: Save secondary phone / default text
                .apply()

            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // ⭐ UPDATED: Now also receives phoneField & phoneField2
    private fun loadUserProfile(
        nameField: EditText,
        phoneField: EditText,
        phoneField2: EditText
    ) {
        // ⭐ UPDATED: Selecting phone2 also
        val cursor = dbHelper.readableDatabase.rawQuery(
            "SELECT name, phone, phone2, image_uri FROM users WHERE email=?",
            arrayOf(currentEmail)
        )
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val phone1 = cursor.getString(1)
            val phone2 = cursor.getString(2)   // may be null or "Phone number not set"
            val uriStr = cursor.getString(3)

            nameField.setText(name)
            phoneField.setText(phone1)

            // ⭐ NEW: Handle phone2 display
            if (!phone2.isNullOrEmpty() && phone2 != "Phone number not set") {
                phoneField2.setText(phone2)
            } else {
                phoneField2.setText("")
                phoneField2.hint = "Phone number not set"
            }

            if (!uriStr.isNullOrEmpty()) {
                imageUri = Uri.parse(uriStr)
                profileImage.setImageURI(imageUri)
            } else {
                profileImage.setImageResource(R.drawable.ic_default_user)
            }
        }
        cursor.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            profileImage.setImageURI(imageUri)
        }
    }
}
