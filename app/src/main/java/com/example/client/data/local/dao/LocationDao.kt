package com.example.client.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.client.data.local.entities.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLocation (location: LocationEntity)

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocation(id: Int): LocationEntity

    @Query("SELECT * FROM locations")
    suspend fun getLocations(): List<LocationEntity>
}