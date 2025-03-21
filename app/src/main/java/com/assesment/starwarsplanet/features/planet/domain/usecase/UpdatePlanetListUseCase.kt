package com.assesment.starwarsplanet.features.planet.domain.usecase

import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.dto.PlanetResult
import javax.inject.Inject

/**
 * Use case responsible for updating the planet list by accumulating results
 * from multiple API responses. This ensures that previously fetched planets
 * are retained while new ones are added.
 */
class UpdatePlanetListUseCase @Inject constructor() {

    // Holds the accumulated list of planet results
    private val resultList = ArrayList<PlanetResult>()

    /**
     * Updates the planet list by appending new results to the existing list.
     *
     * @param planet The new [Planet] object containing additional results.
     * @return The updated [Planet] object with accumulated results.
     */
    operator fun invoke(planet: Planet): Planet {
        resultList.addAll(planet.results) // Add new results to the existing list

        return planet.also {
            it.results = resultList // Assign the updated result list to the planet
        }
    }
}
