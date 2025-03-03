package com.merteroglu286.parkfind.di

import com.merteroglu286.parkfind.data.database.ParkDatabase
import android.content.Context
import com.merteroglu286.parkfind.data.repository.ParkRepositoryImpl
import com.merteroglu286.parkfind.domain.repository.ParkRepository
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
    fun provideDatabase(@ApplicationContext context: Context): ParkDatabase {
        return ParkDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideParkRepository(database: ParkDatabase): ParkRepository {
        return ParkRepositoryImpl(database.parkDao())
    }
}
