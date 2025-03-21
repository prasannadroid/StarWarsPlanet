package com.assesment.starwarsplanet.features.planet.data.dto

data class Planet(
    val count: Int,
    val next: String,
    val previous: String,
    var results: List<PlanetResult>
)
