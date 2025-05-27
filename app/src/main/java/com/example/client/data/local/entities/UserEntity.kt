package com.example.client.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rol : String,
    val userLogged : String
)