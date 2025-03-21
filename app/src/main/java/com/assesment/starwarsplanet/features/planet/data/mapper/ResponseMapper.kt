package com.assesment.starwarsplanet.features.planet.data.mapper

import android.util.Log
import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.core.remote.response.PlanetResponse
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.dto.PlanetResult
import com.google.gson.Gson
import javax.inject.Inject

class ResponseMapper @Inject constructor(private val gson: Gson) {
    private val tag = this::class.qualifiedName

    // map planet response object to Planet DTO
    fun mapToPlanet(planetResponse: PlanetResponse): Planet? {
        return try {
            gson.fromJson(gson.toJson(planetResponse), Planet::class.java)
        } catch (e: Exception) {
            Log.e(tag, "Error mapping JSON response: ${e.message}", e)
            null
        }
    }

    fun getPlanetFromEntity(planetWithResult: PlanetWithResults): Planet {
        return Planet(
            results = planetWithResult.results.map {
                PlanetResult(it.name, it.orbitalPeriod, it.climate, it.gravity)
            },
            count = planetWithResult.planet.count,
            next = planetWithResult.planet.next.orEmpty(),
            previous = planetWithResult.planet.previous.orEmpty()
        )
    }

}

