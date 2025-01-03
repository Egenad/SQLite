package es.ua.eps.sqlite.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

const val ROOM_DB_NAME = "room_sqlite"

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {

        private var _instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            if (_instance == null) {
                try{
                    val builder = databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, ROOM_DB_NAME
                    ).allowMainThreadQueries()
                    _instance = builder.build()
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            return _instance!!
        }

        fun resetDatabase() {
            _instance?.clearAllTables()
            _instance = null
        }
    }
}