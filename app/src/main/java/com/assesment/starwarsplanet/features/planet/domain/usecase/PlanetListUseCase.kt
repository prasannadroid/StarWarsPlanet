package com.assesment.starwarsplanet.features.planet.domain.usecase

import android.util.Log
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case responsible for fetching a list of planets from the repository.
 * It handles potential errors and ensures data is retrieved properly.
 *
 * @param planetRepository The repository that provides planet data.
 */
class PlanetListUseCase @Inject constructor(private val planetRepository: PlanetRepository) {

    private val tag = this::class.qualifiedName

    /**
     * Fetches planets from the repository based on the given page number.
     *
     * @param pageNumber The page number to fetch data for.
     * @return A [Flow] emitting [APIResult] containing either planet data or an error.
     */
    suspend operator fun invoke(pageNumber: Int): Flow<APIResult<Planet>>? {
        return try {
            planetRepository.getPlanets(pageNumber)
        } catch (e: Exception) {
            Log.e(tag, "Error fetching planets: ${e.message}", e)
            flow { emit(APIResult.Error(e)) }
        }
    }
}
