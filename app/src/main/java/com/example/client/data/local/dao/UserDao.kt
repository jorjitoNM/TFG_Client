package com.example.client.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.client.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser (user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username : String): UserEntity

    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<UserEntity>
}