package com.assesment.starwarsplanet.features.planet.data.repository

import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.local.PlanetEntity
import com.assesment.starwarsplanet.core.local.PlanetResultEntity
import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.core.remote.api.APIService
import com.assesment.starwarsplanet.core.remote.response.httpError
import com.assesment.starwarsplanet.core.services.NetworkService
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Implementation of [PlanetRepository] that manages fetching planet data from the remote API
 * and caching it in a local database. This class handles both online and offline scenarios.
 */
class PlanetRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val localDaoService: PlanetDao,
    private val responseMapper: ResponseMapper,
    private val networkService: NetworkService,
) : PlanetRepository {

    /**
     * Fetches planets data.
     * - If the device is online, it fetches data from the remote API.
     * - If offline:
     *   - On the first page, it attempts to retrieve cached data.
     *   - For subsequent pages, it returns an error as no further cached data is available.
     *
     * @param page The page number for pagination.
     * @return A flow emitting [APIResult] containing planet data or an error.
     */
    override suspend fun getPlanets(page: Int): Flow<APIResult<Planet>> = flow {
        if (networkService.isInternetAvailable()) {
            // Fetch data from the remote server
            getRemotePlanets(page).collect {
                emit(it) // Emit the response (success or error)
            }
        } else {
            // Offline mode: Attempt to retrieve cached data
            if (page == 1) {
                val cachedPlanet = getCashedPlanet()
                if (cachedPlanet != null) {
                    emit(APIResult.Success(responseMapper.getPlanetFromEntity(cachedPlanet)))
                } else {
                    emit(APIResult.Error(Throwable("No cached data available")))
                }
            } else {
                emit(APIResult.Error(Throwable("No data found for this page")))
            }
        }
    }.flowOn(Dispatchers.IO) // Run on IO thread for better performance

    /**
     * Fetches planets from the remote API.
     * - On success: Maps the response, saves it to the database, and emits the result.
     * - On failure: Emits an error (network issue, API error, or response mapping failure).
     *
     * @param page The page number to fetch.
     * @return A flow emitting [APIResult] containing the planet data or an error.
     */
    override suspend fun getRemotePlanets(page: Int): Flow<APIResult<Planet>> = flow {
        try {
            val response = apiService.getAllPlanets(page)

            if (response.isSuccessful) {
                response.body()?.let {
                    val planet = responseMapper.mapToPlanet(it)
                    if (planet != null) {
                        // if initial call older cache will be cleared
                        if (page == 1) {
                            deleteOlderCache()
                        }
                        // Emit successful result
                        emit(APIResult.Success(planet))
                        savePlanet(planet) // Cache the data
                    } else {
                        emit(APIResult.Error(Throwable("Failed to map API response to Planet object")))
                    }
                } ?: emit(APIResult.Error(Throwable("Response body is null")))
            } else {
                emit(httpError(response.message(), response.code()))
            }
        } catch (e: Exception) {
            emit(APIResult.Error(e)) // Emit error on exception (e.g., network failure)
        }
    }.flowOn(Dispatchers.IO) // Run in IO dispatcher

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
        localDaoService.insertOrUpdatePlanet(planetEntity) // Save planet metadata
        localDaoService.insertOrUpdatePlanetResults(resultEntities) // Save planet results
    }

    /**
     * Retrieves cached planet data from the local database.
     *
     * @return A [PlanetWithResults] object containing the cached data or null if not available.
     */
    override suspend fun getCashedPlanet(): PlanetWithResults? {
        return localDaoService.getPlanetWithResults()
    }

    /**
     * Deletes all cached planet results from the database.
     *
     * This function clears the stored planet results to ensure only the most recent data is kept.
     */
    override suspend fun deleteOlderCache() {
        localDaoService.deleteAllPlanetResults()
    }

}
