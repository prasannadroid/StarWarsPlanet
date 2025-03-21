package com.assesment.starwarsplanet.core.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlanetEntity::class, PlanetResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlanetDatabase : RoomDatabase() {
    abstract fun planetDao(): PlanetDao
}