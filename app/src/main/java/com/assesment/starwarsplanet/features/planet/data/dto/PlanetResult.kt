package com.assesment.starwarsplanet.features.planet.data.dto

import com.google.gson.annotations.SerializedName

class PlanetResult(
    val name: String,
    @SerializedName("orbital_period")
    val orbitalPeriod: String,
    val climate: String,
    val gravity: String,
)

// Utility function to handle "N/A" replacement
fun String.formatValue(): String = this.replace("N/A", "Not Available")