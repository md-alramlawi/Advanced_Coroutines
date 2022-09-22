package com.ramalwi.flowapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ramalwi.plant.models.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * from plants ORDER BY name")
    fun getPlantsFlow(): Flow<List<Plant>>

    @Query("SELECT * from plants WHERE growZoneNumber = :growZoneNumber ORDER BY name")
    fun getPlantsWithGrowZoneNumberFlow(growZoneNumber: Int): Flow<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)
}