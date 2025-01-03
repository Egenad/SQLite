package es.ua.eps.sqlite.sql

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val fullName: String,
    val email: String
)