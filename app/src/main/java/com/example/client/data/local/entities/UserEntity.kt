package com.example.client.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val id : Int = 0, val username: String, val rol: String)