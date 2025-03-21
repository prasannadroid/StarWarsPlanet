package com.assesment.starwarsplanet

import com.assesment.starwarsplanet.features.planet.data.dto.PlanetResult

class TestHelper {

    companion object {
        fun createPlanet(name: String): PlanetResult {
            return PlanetResult(name, "", "", "")
        }
    }

}