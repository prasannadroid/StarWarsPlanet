package com.assesment.starwarsplanet.core.hilt

import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResponseMapperModule {
    @Provides
    @Singleton
    fun providePlanetMapper(gson: Gson): ResponseMapper {
        return ResponseMapper(gson)
    }
}