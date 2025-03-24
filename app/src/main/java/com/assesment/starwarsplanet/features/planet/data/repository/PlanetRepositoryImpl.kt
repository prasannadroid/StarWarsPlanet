package com.assesment.starwarsplanet.features.planet.data.repository

import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.core.services.NetworkService
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.domain.repository.LocalDataRepository
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
    private val localDataRepository: LocalDataRepository,
    private val remoteDataRepository: RemoteDataRepositoryImpl,
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
            remoteDataRepository.getRemotePlanets(page).collect {
                emit(it) // Emit the response (success or error)
            }
        } else {
            // Offline mode: Attempt to retrieve cached data
            if (page == 1) {
                val cachedPlanet = localDataRepository.getCashedPlanet()
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


}
