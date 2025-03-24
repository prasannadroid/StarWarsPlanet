package com.assesment.starwarsplanet.features.planet.data.repository

import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.core.remote.api.APIService
import com.assesment.starwarsplanet.core.remote.response.httpError
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.domain.repository.LocalDataRepository
import com.assesment.starwarsplanet.features.planet.domain.repository.RemoteDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RemoteDataRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val localDataRepository: LocalDataRepository,
    private val responseMapper: ResponseMapper
) : RemoteDataRepository {
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
                            localDataRepository.deleteOlderCache()
                        }
                        // Emit successful result
                        emit(APIResult.Success(planet))
                        localDataRepository.savePlanet(planet) // Cache the data
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

}