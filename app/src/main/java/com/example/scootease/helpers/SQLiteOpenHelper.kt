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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DBHelper(context: Context) : SQLiteOpenHelper(context, "scooteaseDB.db", null, 2) {

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

        // Tabel Motor
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

        // Tabel Booking
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

        // --- PERUBAHAN ---
        // Masukkan data motor awal ke dalam database saat dibuat
        insertInitialBikes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALAMAT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BIKES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // --- FUNGSI UNTUK PENGGUNA ---
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

    // --- FUNGSI UNTUK ALAMAT ---
    fun insertAlamat(alamat: Alamat): Boolean { /* ... */ return true }
    private fun getAlamatByUser(userId: Int): List<Alamat> { /* ... */ return emptyList() }


    // --- PERUBAHAN ---
    // --- FUNGSI UNTUK MOTOR & BOOKING ---
    private fun insertInitialBikes(db: SQLiteDatabase) {
        val bikes = listOf(
            Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario, BikeStatus.UNAVAILABLE, BikeType.MATIC),
            Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC),
            Bike(3, "Honda Scoopy", "110cc · Auto", "75k", 4.9, R.drawable.honda_scoopy, BikeStatus.AVAILABLE, BikeType.MATIC),
            Bike(4, "Honda PCX", "150cc · Auto", "150k", 4.7, R.drawable.honda_pcx, BikeStatus.UNAVAILABLE, BikeType.MATIC),
            Bike(5, "Harley Sportster 48", "1200cc · Manual", "2000k", 4.6, R.drawable.harley_48, BikeStatus.AVAILABLE, BikeType.MANUAL),
            Bike(6, "BMW R 1200 GS", "1200cc · Manual", "5000k", 4.5, R.drawable.bmw_r1200gs, BikeStatus.AVAILABLE, BikeType.MANUAL),
            Bike(7, "Harley Road Glide", "1800cc · Manual", "5500k", 4.5, R.drawable.harley_rg, BikeStatus.AVAILABLE, BikeType.MANUAL)
        )
        bikes.forEach { bike ->
            val cv = ContentValues().apply {
                put("id", bike.id)
                put("name", bike.name)
                put("specs", bike.specs)
                put("price", bike.price)
                put("rating", bike.rating)
                put("imageRes", bike.imageRes)
                put("status", bike.status.name)
                put("type", bike.type.name)
            }
            db.insert(TABLE_BIKES, null, cv)
        }
    }

    suspend fun getAllBikes(): List<Bike> = withContext(Dispatchers.IO) {
        val db = writableDatabase // Gunakan writable agar bisa insert jika perlu

        // Langkah 1: Cek apakah tabel bikes kosong
        val countCursor = db.rawQuery("SELECT count(*) FROM $TABLE_BIKES", null)
        countCursor.moveToFirst()
        val count = countCursor.getInt(0)
        countCursor.close()

        // Langkah 2: Jika kosong, panggil fungsi untuk mengisi data
        if (count == 0) {
            insertInitialBikes(db)
        }

        // Langkah 3: Sekarang, lanjutkan mengambil data seperti biasa
        val bikeList = mutableListOf<Bike>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_BIKES", null)
        if (cursor.moveToFirst()) {
            do {
                bikeList.add(
                    Bike(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        specs = cursor.getString(cursor.getColumnIndexOrThrow("specs")),
                        price = cursor.getString(cursor.getColumnIndexOrThrow("price")),
                        rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                        imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes")),
                        status = BikeStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))),
                        type = BikeType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("type")))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return@withContext bikeList
    }

    private fun getBikeById(db: SQLiteDatabase, bikeId: Int): Bike? {
        // Gunakan cursor yang aman dengan 'use' yang akan menutupnya secara otomatis
        db.rawQuery("SELECT * FROM $TABLE_BIKES WHERE id = ?", arrayOf(bikeId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                return Bike(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    specs = cursor.getString(cursor.getColumnIndexOrThrow("specs")),
                    price = cursor.getString(cursor.getColumnIndexOrThrow("price")),
                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                    imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageRes")),
                    status = BikeStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))),
                    type = BikeType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("type")))
                )
            }
        }
        return null
    }

  suspend  fun insertBooking(booking: Booking): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", booking.id)
            put("bike_id", booking.bike.id)
            put("start_date", booking.startDate)
            put("end_date", booking.endDate)
            put("total_price", booking.totalPrice)
            put("status", booking.status.name)
        }
        val result = db.insert(TABLE_BOOKINGS, null, cv)
        db.close()
        return result != -1L
    }

  suspend  fun deleteBooking(bookingId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_BOOKINGS, "id=?", arrayOf(bookingId))
        db.close()
        return result > 0
    }

  suspend  fun updateBookingStatus(bookingId: String, newStatus: BookingStatus): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("status", newStatus.name)
        }
        val result = db.update(TABLE_BOOKINGS, cv, "id=?", arrayOf(bookingId))
        db.close()
        return result > 0
    }

    suspend fun getAllBookings(): List<Booking> = withContext(Dispatchers.IO) {
        val bookingList = mutableListOf<Booking>()
        val db = readableDatabase
        // Gunakan 'use' untuk memastikan cursor selalu ditutup
        db.rawQuery("SELECT * FROM $TABLE_BOOKINGS", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val bikeId = cursor.getInt(cursor.getColumnIndexOrThrow("bike_id"))
                    // Teruskan instance DB yang sudah ada ke getBikeById
                    getBikeById(db, bikeId)?.let { bike ->
                        bookingList.add(
                            Booking(
                                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                                bike = bike,
                                startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date")),
                                endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date")),
                                totalPrice = cursor.getString(cursor.getColumnIndexOrThrow("total_price")),
                                status = BookingStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status")))
                            )
                        )
                    }
                } while (cursor.moveToNext())
            }
        }
        db.close() // Tutup koneksi setelah semua selesai
        return@withContext bookingList
    }
}