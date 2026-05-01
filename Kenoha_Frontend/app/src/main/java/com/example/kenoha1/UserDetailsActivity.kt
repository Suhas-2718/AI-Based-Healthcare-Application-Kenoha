//package com.example.kenoha1
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import android.view.View
//
//class UserDetailsActivity : AppCompatActivity() {
//
//    private lateinit var dbHelper: UserDatabaseHelper
//    private lateinit var profileImage: ImageView
//    private lateinit var nameField: EditText
//    private lateinit var phoneField: EditText
//    private lateinit var editButton: ImageView
//    private lateinit var saveButton: Button
//    private lateinit var removeImageButton: ImageView
//    private var imageUri: Uri? = null
//    private var currentEmail: String = ""
//
//    private val PICK_IMAGE_REQUEST = 202
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_details)
//
//        dbHelper = UserDatabaseHelper(this)
//
//        // Initialize views
//        profileImage = findViewById(R.id.profileImage)
//        nameField = findViewById(R.id.userName)
//        phoneField = findViewById(R.id.userPhone)
//        editButton = findViewById(R.id.editIcon)
//        saveButton = findViewById(R.id.saveButton)
//        removeImageButton = findViewById(R.id.removeImageButton)
//
//        // Get logged-in user's email
//        currentEmail = intent.getStringExtra("email") ?: ""
//
//        setEditingEnabled(false)
//        loadUserDetails()
//
//        // Edit button logic
//        editButton.setOnClickListener {
//            setEditingEnabled(true)
//            editButton.visibility = View.GONE
//            saveButton.visibility = View.VISIBLE
//            removeImageButton.visibility = View.VISIBLE
//        }
//
//        // Pick profile image
//        profileImage.setOnClickListener {
//            if (nameField.isEnabled) {
//                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                    addCategory(Intent.CATEGORY_OPENABLE)
//                    type = "image/*"
//                    addFlags(
//                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
//                                Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                }
//                startActivityForResult(intent, PICK_IMAGE_REQUEST)
//            }
//        }
//
//        // Remove image button
//        removeImageButton.setOnClickListener {
//            if (nameField.isEnabled) {
//                imageUri = null
//                profileImage.setImageResource(R.drawable.ic_default_user)
//                Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Save button
//        saveButton.setOnClickListener {
//            val name = nameField.text.toString().trim()
//            val phone = phoneField.text.toString().trim()
//
//            if (name.isEmpty() || phone.isEmpty()) {
//                Toast.makeText(this, "Name and phone cannot be empty", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val formattedPhone = if (!phone.startsWith("+91")) "+91 $phone" else phone
//
//            dbHelper.updateProfile(currentEmail, name, formattedPhone, imageUri?.toString())
//            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//
//            setEditingEnabled(false)
//            saveButton.visibility = View.GONE
//            removeImageButton.visibility = View.GONE
//            editButton.visibility = View.VISIBLE
//        }
//    }
//
//    // Enable or disable editing
//    private fun setEditingEnabled(enabled: Boolean) {
//        nameField.isEnabled = enabled
//        phoneField.isEnabled = enabled
//        saveButton.isEnabled = enabled
//        removeImageButton.isEnabled = enabled
//    }
//
//    // Load user data safely
//    private fun loadUserDetails() {
//        if (currentEmail.isEmpty()) return
//
//        val db = dbHelper.readableDatabase
//        val cursor = db.rawQuery(
//            "SELECT name, phone, image_uri FROM users WHERE email=?",
//            arrayOf(currentEmail)
//        )
//
//        if (cursor.moveToFirst()) {
//            val name = cursor.getString(0)
//            val phone = cursor.getString(1)
//            val imageUriStr = cursor.getString(2)
//
//            nameField.setText(name)
//            phoneField.setText(phone)
//
//            if (!imageUriStr.isNullOrEmpty()) {
//                try {
//                    imageUri = Uri.parse(imageUriStr)
//                    // Verify access
//                    val resolver = applicationContext.contentResolver
//                    resolver.openInputStream(imageUri!!)?.use {
//                        profileImage.setImageURI(imageUri)
//                    }
//                } catch (e: SecurityException) {
//                    e.printStackTrace()
//                    Toast.makeText(
//                        this,
//                        "Welcome!!!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    profileImage.setImageResource(R.drawable.ic_default_user)
//                }
//            } else {
//                profileImage.setImageResource(R.drawable.ic_default_user)
//            }
//        } else {
//            nameField.setText("")
//            phoneField.setText("")
//            profileImage.setImageResource(R.drawable.ic_default_user)
//        }
//
//        cursor.close()
//        db.close()
//    }
//
//    // Handle image picker result safely
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            val uri = data.data
//            if (uri != null) {
//                try {
//                    val resolver = applicationContext.contentResolver
//                    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    resolver.takePersistableUriPermission(uri, takeFlags)
//                    profileImage.setImageURI(uri)
//                    imageUri = uri
//                } catch (e: SecurityException) {
//                    e.printStackTrace()
//                    Toast.makeText(this, "Failed to save image access", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}

package com.example.kenoha1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var profileImage: ImageView
    private lateinit var nameField: EditText
    private lateinit var phoneField: EditText
    private lateinit var phoneField2: EditText     // ⭐ NEW: Second phone field
    private lateinit var editButton: ImageView
    private lateinit var saveButton: Button
    private lateinit var removeImageButton: ImageView
    private var imageUri: Uri? = null
    private var currentEmail: String = ""

    private val PICK_IMAGE_REQUEST = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        dbHelper = UserDatabaseHelper(this)

        // Initialize views
        profileImage = findViewById(R.id.profileImage)
        nameField = findViewById(R.id.userName)
        phoneField = findViewById(R.id.userPhone)
        phoneField2 = findViewById(R.id.userPhone2)     // ⭐ NEW: bind Emergency Contact 2
        editButton = findViewById(R.id.editIcon)
        saveButton = findViewById(R.id.saveButton)
        removeImageButton = findViewById(R.id.removeImageButton)

        // Get logged-in user's email
        currentEmail = intent.getStringExtra("email") ?: ""

        setEditingEnabled(false)
        loadUserDetails()

        // Edit button logic
        editButton.setOnClickListener {
            setEditingEnabled(true)
            editButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
            removeImageButton.visibility = View.VISIBLE
        }

        // Pick profile image
        profileImage.setOnClickListener {
            if (nameField.isEnabled) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                    addFlags(
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
        }

        // Remove image button
        removeImageButton.setOnClickListener {
            if (nameField.isEnabled) {
                imageUri = null
                profileImage.setImageResource(R.drawable.ic_default_user)
                Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        saveButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val phone1 = phoneField.text.toString().trim()
            var phone2 = phoneField2.text.toString().trim()   // ⭐ NEW

            if (name.isEmpty() || phone1.isEmpty()) {
                Toast.makeText(this, "Name and primary phone cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formattedPhone1 = if (!phone1.startsWith("+91")) "+91 $phone1" else phone1

            // ⭐ NEW: Handle optional second phone
            val formattedPhone2: String = if (phone2.isNotEmpty()) {
                if (!phone2.startsWith("+91")) "+91 $phone2" else phone2
            } else {
                "Phone number not set"
            }

            // ⭐ UPDATED: call updateProfile with phone2 also
            dbHelper.updateProfile(currentEmail, name, formattedPhone1, formattedPhone2, imageUri?.toString())
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()

            setEditingEnabled(false)
            saveButton.visibility = View.GONE
            removeImageButton.visibility = View.GONE
            editButton.visibility = View.VISIBLE
        }
    }

    // Enable or disable editing
    private fun setEditingEnabled(enabled: Boolean) {
        nameField.isEnabled = enabled
        phoneField.isEnabled = enabled
        phoneField2.isEnabled = enabled        // ⭐ NEW
        saveButton.isEnabled = enabled
        removeImageButton.isEnabled = enabled
    }

    // Load user data safely
    private fun loadUserDetails() {
        if (currentEmail.isEmpty()) return

        val db = dbHelper.readableDatabase

        // ⭐ UPDATED: Also select phone2
        val cursor = db.rawQuery(
            "SELECT name, phone, phone2, image_uri FROM users WHERE email=?",
            arrayOf(currentEmail)
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val phone1 = cursor.getString(1)
            val phone2 = cursor.getString(2)        // ⭐ NEW
            val imageUriStr = cursor.getString(3)

            nameField.setText(name)
            phoneField.setText(phone1)

            // ⭐ NEW: Handle second phone display
            if (!phone2.isNullOrEmpty() && phone2 != "Phone number not set") {
                phoneField2.setText(phone2)
            } else {
                phoneField2.setText("")
                phoneField2.hint = "Phone number not set"
            }

            if (!imageUriStr.isNullOrEmpty()) {
                try {
                    imageUri = Uri.parse(imageUriStr)
                    // Verify access
                    val resolver = applicationContext.contentResolver
                    resolver.openInputStream(imageUri!!)?.use {
                        profileImage.setImageURI(imageUri)
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Welcome!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    profileImage.setImageResource(R.drawable.ic_default_user)
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_default_user)
            }
        } else {
            nameField.setText("")
            phoneField.setText("")
            phoneField2.setText("")                     // ⭐ NEW
            phoneField2.hint = "Phone number not set"   // ⭐ NEW
            profileImage.setImageResource(R.drawable.ic_default_user)
        }

        cursor.close()
        db.close()
    }

    // Handle image picker result safely
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                try {
                    val resolver = applicationContext.contentResolver
                    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    resolver.takePersistableUriPermission(uri, takeFlags)
                    profileImage.setImageURI(uri)
                    imageUri = uri
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to save image access", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

