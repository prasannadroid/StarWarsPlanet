package com.assesment.starwarsplanet.features.planet.domain.repository

import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.features.planet.data.dto.Planet

/**
 * Interface for managing local data storage related to planets.
 *
 * This repository handles caching operations, including saving, retrieving,
 * and deleting outdated data from the local database.
 */
interface LocalDataRepository {
    /**
     * Saves the given planet data to the local database.
     *
     * @param planet The planet data to be stored.
     */
    suspend fun savePlanet(planet: Planet)

    /**
     * Retrieves cached planet data from the local database.
     *
     * @return A [PlanetWithResults] object containing cached data,
     *         or null if no data is available.
     */
    suspend fun getCashedPlanet(): PlanetWithResults?

    /**
     * Deletes outdated cached data from the database.
     *
     * This function removes old or unnecessary cached data to free up resources and ensure the app
     * uses up-to-date information.
     */
    suspend fun deleteOlderCache()
}