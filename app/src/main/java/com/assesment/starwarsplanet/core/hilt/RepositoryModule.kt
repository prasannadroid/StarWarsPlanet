package com.assesment.starwarsplanet.core.hilt

import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.remote.api.APIService
import com.assesment.starwarsplanet.core.services.NetworkService
import com.assesment.starwarsplanet.features.planet.data.repository.PlanetRepositoryImpl
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePlanetRepository(
        apiService: APIService,
        localDaoService: PlanetDao,
        responseMapper: ResponseMapper,
        networkService: NetworkService
    ): PlanetRepository {
        return PlanetRepositoryImpl(apiService, localDaoService, responseMapper, networkService)
    }
}