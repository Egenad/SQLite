package es.ua.eps.sqlite.sql

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre_usuario: String,
    val password: String,
    val nombre_completo: String,
    val email: String
)