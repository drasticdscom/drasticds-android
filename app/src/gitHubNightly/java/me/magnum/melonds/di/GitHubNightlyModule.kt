package com.drasticds.emulator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.drasticds.emulator.domain.repositories.UpdatesRepository
import com.drasticds.emulator.github.GitHubApi
import com.drasticds.emulator.github.repositories.GitHubNightlyUpdatesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitHubNightlyModule {

    @Provides
    @Singleton
    fun provideUpdatesRepository(@ApplicationContext context: Context, gitHubApi: GitHubApi): UpdatesRepository {
        val gitHubPreferences = context.getSharedPreferences("preferences-github", Context.MODE_PRIVATE)
        return GitHubNightlyUpdatesRepository(gitHubApi, gitHubPreferences)
    }
}