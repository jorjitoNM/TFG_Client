package com.example.client.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.client.data.local.dao.LocationDao
import com.example.client.data.local.dao.UserDao
import com.example.client.data.local.entities.LocationEntity
import com.example.client.data.local.entities.UserEntity

@Database(
    entities = [LocationEntity::class, UserEntity::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val locationDao: LocationDao
    abstract val userDao: UserDao
}

