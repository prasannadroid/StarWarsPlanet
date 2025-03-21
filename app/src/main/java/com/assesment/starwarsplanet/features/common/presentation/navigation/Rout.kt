package com.assesment.starwarsplanet.features.common.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object PlanetListRoute

@Serializable
data class PlanetDetailsRoute(
    val planetName: String,
    val orbitalPeriod: String,
    val gravity: String,
    val index: Int
) {
    companion object {
        fun createRoute(planetName: String, orbitalPeriod: String, gravity: String, index: Int) =
            "detail/$planetName/$orbitalPeriod/$gravity/$index"
    }
}