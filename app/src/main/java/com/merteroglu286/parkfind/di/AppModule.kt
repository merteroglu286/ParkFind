package com.merteroglu286.parkfind.di

import android.content.Context
import com.merteroglu286.parkfind.data.preferences.PreferencesImpl
import com.merteroglu286.parkfind.domain.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePrefHelper(@ApplicationContext context: Context): Preferences {
        return PreferencesImpl(context)
    }

}