package com.example.client.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.client.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userLogged = :userLogged ORDER BY timestamp DESC")
    suspend fun getRecentUsers(userLogged: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE username = :username AND userLogged = :userLogged")
    suspend fun deleteUserByUsername(username: String, userLogged: String)
}
