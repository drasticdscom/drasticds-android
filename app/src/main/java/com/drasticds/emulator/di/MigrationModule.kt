package com.drasticds.emulator.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import com.drasticds.emulator.common.DirectoryAccessValidator
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.domain.repositories.RomsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.impl.RomIconProvider
import com.drasticds.emulator.migrations.*
import com.drasticds.emulator.migrations.helper.GenericJsonArrayMigrationHelper

@Module
@InstallIn(SingletonComponent::class)
object MigrationModule {

    @Provides
    fun provideJsonArrayMigrationHelper(@ApplicationContext context: Context, gson: Gson): GenericJsonArrayMigrationHelper {
        return GenericJsonArrayMigrationHelper(context, gson)
    }

    @Provides
    fun provideMigration(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences,
        romIconProvider: RomIconProvider,
        romsRepository: RomsRepository,
        settingsRepository: SettingsRepository,
        directoryAccessValidator: DirectoryAccessValidator,
        uriHandler: UriHandler,
        gson: Gson,
        json: Json,
        genericJsonArrayMigrationHelper: GenericJsonArrayMigrationHelper,
    ): Migrator {

        return Migrator(context, sharedPreferences).apply {
            registerMigration(Migration6to7(sharedPreferences))
            registerMigration(Migration7to8(context))
            registerMigration(Migration14to15(romIconProvider))
            registerMigration(Migration16to17(romsRepository))
            registerMigration(Migration20to21(settingsRepository, romsRepository, directoryAccessValidator))
            registerMigration(Migration21to22(context, gson, uriHandler))
            registerMigration(Migration24to25(genericJsonArrayMigrationHelper, context))
            registerMigration(Migration25to26(genericJsonArrayMigrationHelper))
            registerMigration(Migration30to31(genericJsonArrayMigrationHelper))
            registerMigration(Migration31to32(context, genericJsonArrayMigrationHelper))
            registerMigration(Migration33to34(context, json))
            registerMigration(Migration34to35(context))
            registerMigration(Migration35to36(context, genericJsonArrayMigrationHelper))
            registerMigration(Migration36to37(genericJsonArrayMigrationHelper))
        }
    }
}