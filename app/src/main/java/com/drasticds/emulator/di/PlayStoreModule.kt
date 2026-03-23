package com.drasticds.emulator.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.drasticds.emulator.domain.repositories.UpdatesRepository
import com.drasticds.emulator.domain.services.UpdateInstallManager
import com.drasticds.emulator.playstore.PlayStoreUpdatesRepository
import com.drasticds.emulator.services.PlayStoreUpdateInstallManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayStoreModule {
    @Provides
    @Singleton
    fun provideUpdatesRepository(): UpdatesRepository {
        return PlayStoreUpdatesRepository()
    }

    @Provides
    @Singleton
    fun provideUpdateInstallManager(): UpdateInstallManager {
        return PlayStoreUpdateInstallManager()
    }
}