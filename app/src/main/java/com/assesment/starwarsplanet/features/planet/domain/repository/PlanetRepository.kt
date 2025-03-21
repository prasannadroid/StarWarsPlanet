package com.assesment.starwarsplanet.features.planet.domain.repository

import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing planet-related data operations.
 * This includes fetching data from a remote source, saving data locally,
 * retrieving cached data, and providing planet data flow to the UI.
 */
interface PlanetRepository {

    /**
     * Fetches a list of planets from the remote API.
     *
     * @param page The page number to retrieve.
     * @return A Flow emitting [APIResult] containing the planet data or an error.
     */
    suspend fun getRemotePlanets(page: Int): Flow<APIResult<Planet>>

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
     * Retrieves planet data, either from the remote API or the local cache
     * if offline. If the requested page is not available in the cache, an error is returned.
     *
     * @param page The page number to retrieve.
     * @return A Flow emitting [APIResult] containing the planet data or an error.
     */
    suspend fun getPlanets(page: Int): Flow<APIResult<Planet>>

    /**
     * Deletes outdated cached data from the database.
     *
     * This function removes old or unnecessary cached data to free up resources and ensure the app
     * uses up-to-date information.
     */
    suspend fun deleteOlderCache()
}
