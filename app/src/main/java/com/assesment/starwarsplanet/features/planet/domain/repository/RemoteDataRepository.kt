package com.assesment.starwarsplanet.features.planet.domain.repository

import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import kotlinx.coroutines.flow.Flow

interface RemoteDataRepository {
    /**
     * Fetches a list of planets from the remote API.
     *
     * @param page The page number to retrieve.
     * @return A Flow emitting [APIResult] containing the planet data or an error.
     */
    suspend fun getRemotePlanets(page: Int): Flow<APIResult<Planet>>
}