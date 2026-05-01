//package com.example.kenoha1
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//
//class UserDatabaseHelper(context: Context) :
//    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    companion object {
//        private const val DATABASE_NAME = "user_database.db"
//        private const val DATABASE_VERSION = 2
//        private const val TABLE_USERS = "users"
//        private const val COLUMN_ID = "id"
//        private const val COLUMN_EMAIL = "email"
//        private const val COLUMN_PASSWORD = "password"
//        private const val COLUMN_NAME = "name"
//        private const val COLUMN_PHONE = "phone"
//        private const val COLUMN_IMAGE_URI = "image_uri"
//    }
//
//    override fun onCreate(db: SQLiteDatabase) {
//        val createTable = "CREATE TABLE $TABLE_USERS (" +
//                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "$COLUMN_EMAIL TEXT UNIQUE, " +
//                "$COLUMN_PASSWORD TEXT, " +
//                "$COLUMN_NAME TEXT, " +
//                "$COLUMN_PHONE TEXT, " +
//                "$COLUMN_IMAGE_URI TEXT)"
//        db.execSQL(createTable)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
//        onCreate(db)
//    }
//
//    fun addUser(email: String, password: String): Boolean {
//        val db = writableDatabase
//        val values = ContentValues()
//        values.put(COLUMN_EMAIL, email)
//        values.put(COLUMN_PASSWORD, password)
//        val result = db.insert(TABLE_USERS, null, values)
//        db.close()
//        return result != -1L
//    }
//
//    fun getUserName(email: String): String {
//        val db = readableDatabase
//        val cursor = db.rawQuery(
//            "SELECT name FROM $TABLE_USERS WHERE $COLUMN_EMAIL=?",
//            arrayOf(email)
//        )
//        val name = if (cursor.moveToFirst()) cursor.getString(0) ?: "" else ""
//        cursor.close()
//        db.close()
//        return name
//    }
//
//    fun checkUserExists(email: String): Boolean {
//        val db = readableDatabase
//        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=?", arrayOf(email))
//        val exists = cursor.count > 0
//        cursor.close()
//        db.close()
//        return exists
//    }
//
//    fun validateUser(email: String, password: String): Boolean {
//        val db = readableDatabase
//        val cursor = db.rawQuery(
//            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?",
//            arrayOf(email, password)
//        )
//        val isValid = cursor.count > 0
//        cursor.close()
//        db.close()
//        return isValid
//    }
//
//    fun updateProfile(email: String, name: String, phone: String, imageUri: String?) {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put(COLUMN_NAME, name)
//            put(COLUMN_PHONE, phone)
//            put(COLUMN_IMAGE_URI, imageUri)
//        }
//        db.update(TABLE_USERS, values, "$COLUMN_EMAIL=?", arrayOf(email))
//        db.close()
//    }
//}


package com.example.kenoha1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 3             // ⭐ UPDATED: bumped to 3

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_PHONE2 = "phone2"         // ⭐ NEW: second phone column
        private const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // ⭐ UPDATED: added COLUMN_PHONE2 in CREATE TABLE
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_PHONE TEXT, " +
                "$COLUMN_PHONE2 TEXT, " +              // ⭐ NEW
                "$COLUMN_IMAGE_URI TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // ⭐ UPDATED: no more DROP TABLE, we just add missing columns
        if (oldVersion < 3) {
            // Add phone2 column if upgrading from older versions
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_PHONE2 TEXT")
        }
    }

    fun addUser(email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun getUserName(email: String): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_NAME FROM $TABLE_USERS WHERE $COLUMN_EMAIL=?",
            arrayOf(email)
        )
        val name = if (cursor.moveToFirst()) cursor.getString(0) ?: "" else ""
        cursor.close()
        db.close()
        return name
    }

    fun checkUserExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=?",
            arrayOf(email)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun validateUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?",
            arrayOf(email, password)
        )
        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }

    // ⭐ UPDATED: added phone2 as parameter
    fun updateProfile(email: String, name: String, phone: String, phone2: String, imageUri: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PHONE2, phone2)          // ⭐ NEW: store second phone / "Phone number not set"
            put(COLUMN_IMAGE_URI, imageUri)
        }
        db.update(TABLE_USERS, values, "$COLUMN_EMAIL=?", arrayOf(email))
        db.close()
    }
}
