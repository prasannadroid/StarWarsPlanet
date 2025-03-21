package com.assesment.starwarsplanet.core.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlanetDao {
    // Insert or Update Planet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlanet(planet: PlanetEntity)

    // Insert or Update Planet Results (Bulk Insert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlanetResults(results: List<PlanetResultEntity>)

    // Get Planet with its results
    @Transaction
    @Query("SELECT * FROM planet WHERE id = 1 LIMIT 1")
    suspend fun getPlanetWithResults(): PlanetWithResults?

    // Delete all planet results before updating (Optional)
    @Query("DELETE FROM planet_results WHERE planetId = 1")
    suspend fun deleteAllPlanetResults()
}