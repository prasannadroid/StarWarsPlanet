package com.assesment.starwarsplanet.core.hilt

import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.remote.api.APIService
import com.assesment.starwarsplanet.core.services.NetworkService
import com.assesment.starwarsplanet.features.planet.data.repository.PlanetRepositoryImpl
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.data.repository.LocalDataRepositoryImpl
import com.assesment.starwarsplanet.features.planet.data.repository.RemoteDataRepositoryImpl
import com.assesment.starwarsplanet.features.planet.domain.repository.LocalDataRepository
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import com.assesment.starwarsplanet.features.planet.domain.repository.RemoteDataRepository
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
        remoteDataRepository: RemoteDataRepositoryImpl,
        localDataRepository: LocalDataRepository,
        responseMapper: ResponseMapper,
        networkService: NetworkService
    ): PlanetRepository {
        return PlanetRepositoryImpl(
            localDataRepository,
            remoteDataRepository,
            responseMapper,
            networkService
        )
    }

    @Provides
    fun provideRemoteDataRepository(
        apiService: APIService,
        localDataRepository: LocalDataRepository,
        responseMapper: ResponseMapper
    ): RemoteDataRepository {
        return RemoteDataRepositoryImpl(apiService, localDataRepository, responseMapper)
    }

    @Provides
    fun provideLocalDataRepository(
        planetDao: PlanetDao
    ): LocalDataRepository {
        return LocalDataRepositoryImpl(planetDao)
    }
}