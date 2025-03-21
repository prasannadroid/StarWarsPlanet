package com.assesment.starwarsplanet.core.remote.api

import com.assesment.starwarsplanet.core.remote.response.PlanetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("planets")
    suspend fun getAllPlanets(@Query("page") page: Int) : Response<PlanetResponse>
}