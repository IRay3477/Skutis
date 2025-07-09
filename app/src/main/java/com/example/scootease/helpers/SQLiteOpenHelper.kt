package com.example.scootease.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.scootease.R
import com.example.scootease.models.Alamat
import com.example.scootease.activity.Bike
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import com.example.scootease.models.User

class DBHelper(context: Context) : SQLiteOpenHelper(context, "scooteaseDB.db", null, 1) {

    companion object {
        private const val TABLE_USERS = "users"
        private const val TABLE_ALAMAT = "alamat"
        private const val TABLE_BIKES = "bikes"
        private const val TABLE_BOOKINGS = "bookings"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabel Pengguna (dari kode Anda)
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT UNIQUE,
                password TEXT,
                no_hp TEXT,
                tanggal_lahir TEXT,
                role TEXT
            )
        """.trimIndent()
        )

        // Tabel Alamat (dari kode Anda)
        db.execSQL(
            """
            CREATE TABLE $TABLE_ALAMAT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_user INTEGER,
                nama_penerima TEXT,
                no_hp_penerima TEXT,
                jalan TEXT,
                rt TEXT,
                rw TEXT,
                desa_kelurahan TEXT,
                kecamatan TEXT,
                kota_kabupaten TEXT,
                provinsi TEXT,
                kode_pos TEXT,
                catatan TEXT,
                FOREIGN KEY(id_user) REFERENCES $TABLE_USERS(id)
            )
        """.trimIndent()
        )

        // Tabel Motor (tambahan untuk fitur rental)
        db.execSQL(
            """
            CREATE TABLE $TABLE_BIKES (
                id INTEGER PRIMARY KEY,
                name TEXT,
                specs TEXT,
                price TEXT,
                rating REAL,
                imageRes INTEGER,
                status TEXT,
                type TEXT
            )
        """.trimIndent()
        )

        // Tabel Booking (tambahan untuk fitur rental)
        db.execSQL(
            """
            CREATE TABLE $TABLE_BOOKINGS (
                id TEXT PRIMARY KEY,
                bike_id INTEGER,
                start_date TEXT,
                end_date TEXT,
                total_price TEXT,
                status TEXT,
                FOREIGN KEY(bike_id) REFERENCES $TABLE_BIKES(id)
            )
        """.trimIndent()
        )

        // Masukkan data motor awal
        insertInitialBikes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALAMAT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BIKES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // --- FUNGSI UNTUK PENGGUNA (dari kode Anda) ---
    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("password", user.password)
            put("no_hp", user.no_hp)
            put("tanggal_lahir", user.tanggal_lahir)
            put("role", user.role)
        }
        val res = db.insert(TABLE_USERS, null, cv)
        db.close()
        return res != -1L
    }

    fun login(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE email=? AND password=?", arrayOf(email, password))
        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            user = User(
                id = id,
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                no_hp = cursor.getString(cursor.getColumnIndexOrThrow("no_hp")),
                tanggal_lahir = cursor.getString(cursor.getColumnIndexOrThrow("tanggal_lahir")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                alamat = getAlamatByUser(id) // Ambil alamat terkait
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_USERS WHERE email=?", arrayOf(email))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    // --- FUNGSI UNTUK ALAMAT (dari kode Anda) ---
    fun insertAlamat(alamat: Alamat): Boolean { /* ... */ return true }
    private fun getAlamatByUser(userId: Int): List<Alamat> { /* ... */ return emptyList() }


    // --- FUNGSI UNTUK MOTOR & BOOKING (tambahan) ---
    private fun insertInitialBikes(db: SQLiteDatabase) {
        // ... (Logika untuk memasukkan data motor awal, sama seperti sebelumnya)
    }

    fun getAllBikes(): List<Bike> { /* ... */ return emptyList() }
    private fun getBikeById(bikeId: Int): Bike? { /* ... */ return null }
    fun insertBooking(booking: Booking): Boolean { /* ... */ return true }
    fun deleteBooking(bookingId: String): Boolean { /* ... */ return true }
    fun getAllBookings(): List<Booking> { /* ... */ return emptyList() }
}