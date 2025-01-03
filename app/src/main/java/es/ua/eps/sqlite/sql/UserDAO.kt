package es.ua.eps.sqlite.sql

import androidx.room.*

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: UserEntity)

    @Update
    fun updateUser(user: UserEntity): Int

    @Delete
    fun deleteUser(user: UserEntity): Int

    @Query("SELECT * FROM Usuarios")
    fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM Usuarios WHERE nombre_usuario = :username")
    fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM Usuarios WHERE id = :id")
    fun getUserById(id: Int): UserEntity?

    @Query("DELETE FROM Usuarios")
    fun clearTable()
}