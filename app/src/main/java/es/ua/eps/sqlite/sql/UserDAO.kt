package es.ua.eps.sqlite.sql

import androidx.room.*

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Long

    @Update
    fun updateUser(user: UserEntity): Int

    @Delete
    fun deleteUser(user: UserEntity): Int

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): UserEntity?

}