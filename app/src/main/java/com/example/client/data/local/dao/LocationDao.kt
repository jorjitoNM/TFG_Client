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

    @Query("SELECT * FROM locations WHERE userLogged = :userLogged ORDER BY timestamp DESC")
    suspend fun getLocations(userLogged : String): List<LocationEntity>

    @Query("DELETE FROM locations WHERE id = :id AND userLogged = :userLogged")
    suspend fun deleteLocation(id: Int, userLogged: String)
}