package com.assesment.starwarsplanet.features.planet.data.repository

import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.local.PlanetEntity
import com.assesment.starwarsplanet.core.local.PlanetResultEntity
import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.domain.repository.LocalDataRepository

/**
 * Implementation of [LocalDataRepository] for managing local database operations.
 *
 * This class handles caching logic, including saving, retrieving, and deleting
 * planet data using the provided [PlanetDao].
 *
 * @property planetDao The DAO used for database interactions.
 */
class LocalDataRepositoryImpl(private val planetDao: PlanetDao) : LocalDataRepository {
    /**
     * Saves planet data to the local database.
     * - Stores planet metadata.
     * - Stores individual planet result entries.
     *
     * @param planet The [Planet] object to be stored in the database.
     */
    override suspend fun savePlanet(planet: Planet) {
        val planetEntity = PlanetEntity(
            count = planet.count,
            next = planet.next,
            previous = planet.previous
        )
        val resultEntities = planet.results.map {
            PlanetResultEntity(
                name = it.name,
                orbitalPeriod = it.orbitalPeriod,
                climate = it.climate,
                gravity = it.gravity
            )
        }
        // Perform database operations in a transaction
        planetDao.insertOrUpdatePlanet(planetEntity) // Save planet metadata
        planetDao.insertOrUpdatePlanetResults(resultEntities) // Save planet results
    }

    /**
     * Retrieves cached planet data from the local database.
     *
     * @return A [PlanetWithResults] object containing the cached data or null if not available.
     */
    override suspend fun getCashedPlanet(): PlanetWithResults? {
        return planetDao.getPlanetWithResults()
    }

    /**
     * Deletes all cached planet results from the database.
     *
     * This function clears the stored planet results to ensure only the most recent data is kept.
     */
    override suspend fun deleteOlderCache() {
        planetDao.deleteAllPlanetResults()
    }
}