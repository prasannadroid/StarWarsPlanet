package com.assesment.starwarsplanet.features.planet.domain.repository

import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing local data storage related to planets.
 *
 * This repository handles caching operations, including saving, retrieving,
 * and deleting outdated data from the local database.
 */
interface PlanetRepository {

    /**
     * Retrieves planet data, either from the remote API or the local cache
     * if offline. If the requested page is not available in the cache, an error is returned.
     *
     * @param page The page number to retrieve.
     * @return A Flow emitting [APIResult] containing the planet data or an error.
     */
    suspend fun getPlanets(page: Int): Flow<APIResult<Planet>>

}
