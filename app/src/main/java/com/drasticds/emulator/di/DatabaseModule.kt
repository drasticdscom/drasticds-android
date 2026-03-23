package com.drasticds.emulator.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.drasticds.emulator.database.MelonDatabase
import com.drasticds.emulator.database.callback.CustomCheatCreationCallback
import com.drasticds.emulator.database.daos.RetroAchievementsDao
import com.drasticds.emulator.database.migrations.Migration1to2
import com.drasticds.emulator.database.migrations.Migration4to5
import com.drasticds.emulator.database.migrations.Migration5to6
import com.drasticds.emulator.database.migrations.Migration7to8
import com.drasticds.emulator.impl.retroachievements.NoCacheRetroAchievementsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @IntoSet
    fun provideCustomCheatDatabaseCreationCallback(): RoomDatabase.Callback {
        return CustomCheatCreationCallback()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, callbacks: Set<@JvmSuppressWildcards RoomDatabase.Callback>): MelonDatabase {
        return Room.databaseBuilder(context, MelonDatabase::class.java, "melon-database")
            .apply {
                callbacks.forEach {
                    addCallback(it)
                }
            }
            .addMigrations(Migration1to2(), Migration4to5(), Migration5to6(), Migration7to8())
            .build()
    }

    @Provides
    fun provideRAAchievementsDao(database: MelonDatabase): RetroAchievementsDao {
        return NoCacheRetroAchievementsDao(database.achievementsDao())
    }
}