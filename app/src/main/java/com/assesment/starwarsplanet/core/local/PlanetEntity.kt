package com.assesment.starwarsplanet.core.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "planet")
data class PlanetEntity(
    @PrimaryKey val id: Int = 1, // Single planet object
    val count: Int,
    val next: String?,
    val previous: String?
)

@Entity(tableName = "planet_results")
data class PlanetResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planetId: Int = 1, // Foreign key reference (not enforced)
    val name: String,
    val orbitalPeriod: String,
    val climate: String,
    val gravity: String
)


data class PlanetWithResults(
    @Embedded val planet: PlanetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planetId"
    )
    val results: List<PlanetResultEntity>
)