package com.assesment.starwarsplanet.features.planet.data.dto

import com.google.gson.annotations.SerializedName

class PlanetResult(
    val name: String,
    //val rotationPeriod: String,
    @SerializedName("orbital_period")
    val orbitalPeriod: String,
    //val diameter: String,
    val climate: String,
    val gravity: String,
   /* val terrain: String,
    val surfaceWater: String,
    val population: String,
    val residents: List<String>,
    val films: List<String>,
    val created: String,
    val edited: String,
    val url: String,*/
)

// Utility function to handle "N/A" replacement
fun String.formatValue(): String = this.replace("N/A", "Not Available")