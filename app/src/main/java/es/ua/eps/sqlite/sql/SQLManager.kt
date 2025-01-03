package es.ua.eps.sqlite.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SQLManager (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private var instance: SQLManager? = null

        const val DATABASE_NAME = "atm_sqlite.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Usuarios"
        const val COLUMN_ID = "ID"
        const val COLUMN_USERNAME = "nombre_usuario"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "nombre_completo"
        const val COLUMN_EMAIL = "email"

        @Synchronized
        fun getInstance(context: Context): SQLManager {
            if (instance == null) {
                instance = SQLManager(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_FULLNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL
            )
        """
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(username: String, password: String, fullName: String, email: String): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FULLNAME, fullName)
            put(COLUMN_EMAIL, email)
        }

        return db.insert(TABLE_NAME, null, values)
    }

    fun updateUser(id: Int, username: String, password: String, fullName: String, email: String): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FULLNAME, fullName)
            put(COLUMN_EMAIL, email)
        }

        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteUser(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                userList.add(formatUser(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    fun getUserById(id: Int): User? {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = formatUser(cursor)
        }

        cursor.close()

        return user
    }

    fun getUserByUsername(username: String): User? {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            user = formatUser(cursor)
        }

        cursor.close()

        return user
    }

    fun getUserByEmail(email: String): Cursor {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?"
        return db.rawQuery(query, arrayOf(email))
    }

    private fun formatUser(cursor: Cursor): User {
        return User(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        )
    }

    fun backupDatabase(context: Context): Boolean {

        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

            val databasePath = context.getDatabasePath(DATABASE_NAME)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = dateFormat.format(Date())

            val backupFileName = "${todayDate}_${DATABASE_NAME}"
            val externalBackupPath = File(context.getExternalFilesDir(null), backupFileName)

            return try {
                databasePath.inputStream().use { input ->
                    externalBackupPath.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } catch (e: IOException) {
                Log.e("BackupDatabase", "Error al crear el backup: ${e.message}")
                false
            }
        }else return false
    }

    fun restoreDatabase(context: Context): Boolean {
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

            val databasePath = context.getDatabasePath(DATABASE_NAME)

            val backupDir = context.getExternalFilesDir(null)
            val backupFiles =
                backupDir?.listFiles { file -> file.name.endsWith("_${DATABASE_NAME}") }

            if (backupFiles.isNullOrEmpty()) {
                Log.e("RestoreDatabase", "No se encontraron backups")
                return false
            }

            val latestBackup =
                backupFiles.maxByOrNull { it.name.substringBefore("_").replace("-", "").toInt() }

            return try {
                latestBackup?.inputStream()?.use { input ->
                    databasePath.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } catch (e: IOException) {
                Log.e("RestoreDatabase", "Error al restaurar el backup: ${e.message}")
                false
            }
        } else return false
    }

    fun backupRoomDatabase(context: Context): Boolean {
        return try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

                // Close DB
                AppDatabase.getDatabase(context).close()

                val dbFile = context.getDatabasePath(ROOM_DB_NAME)

                val backupDir = File(context.getExternalFilesDir(null), "backup")

                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                val backupDbFile = File(backupDir, ROOM_DB_NAME)

                dbFile.inputStream().use { input ->
                    backupDbFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restoreRoomDatabase(context: Context): Boolean {
        return try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

                // Close DB
                AppDatabase.getDatabase(context).close()

                val backupDir = File(context.getExternalFilesDir(null), "backup")
                val backupDbFile = File(backupDir, ROOM_DB_NAME)

                if (backupDbFile.exists()) {

                    val dbFile = context.getDatabasePath(ROOM_DB_NAME)

                    backupDbFile.inputStream().use { input ->
                        dbFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}