package com.drasticds.emulator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.drasticds.emulator.domain.repositories.UpdatesRepository
import com.drasticds.emulator.github.GitHubApi
import com.drasticds.emulator.github.repositories.GitHubProdUpdatesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitHubProdModule {

    @Provides
    @Singleton
    fun provideUpdatesRepository(@ApplicationContext context: Context, gitHubApi: GitHubApi): UpdatesRepository {
        val gitHubPreferences = context.getSharedPreferences("preferences-github", Context.MODE_PRIVATE)
        return GitHubProdUpdatesRepository(context, gitHubApi, gitHubPreferences)
    }
}