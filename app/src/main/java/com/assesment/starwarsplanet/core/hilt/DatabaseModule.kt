package com.assesment.starwarsplanet.core.hilt

import android.content.Context
import androidx.room.Room
import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.local.PlanetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlanetDatabase {
        return Room.databaseBuilder(
            context,
            PlanetDatabase::class.java,
            "planet.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlanetDao(database: PlanetDatabase): PlanetDao {
        return database.planetDao()
    }
}