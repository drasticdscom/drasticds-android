package com.drasticds.emulator.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Vibrator
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import com.drasticds.emulator.common.romprocessors.RomFileProcessorFactory
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.common.vibration.Api26VibratorDelegate
import com.drasticds.emulator.common.vibration.OldVibratorDelegate
import com.drasticds.emulator.common.vibration.TouchVibrator
import com.drasticds.emulator.database.MelonDatabase
import com.drasticds.emulator.database.daos.RetroAchievementsDao
import com.drasticds.emulator.domain.repositories.*
import com.drasticds.emulator.domain.services.ConfigurationDirectoryVerifier
import com.drasticds.emulator.domain.services.DSiNandManager
import com.drasticds.emulator.domain.services.EmulatorLaunchPreconditionChecker
import com.drasticds.emulator.impl.AndroidDSiNandManager
import com.drasticds.emulator.impl.*
import com.drasticds.emulator.impl.input.ControllerConfigurationFactory
import com.drasticds.emulator.impl.input.DefaultControllerConfigurationFactory
import com.drasticds.emulator.impl.layout.DeviceLayoutDisplayMapper
import com.drasticds.emulator.impl.layout.DefaultLayoutProvider
import com.drasticds.emulator.impl.layout.UILayoutProvider
import com.drasticds.emulator.impl.layout.devicemapper.AynThorLayoutDisplayMapper
import com.drasticds.emulator.impl.layout.devicemapper.DefaultLayoutDisplayMapper
import com.drasticds.emulator.impl.romprocessors.Api24RomFileProcessorFactory
import com.drasticds.emulator.ui.romdetails.RomDetailsUiMapper
import com.drasticds.emulator.rcheevosapi.RAApi
import com.drasticds.emulator.rcheevosapi.RAUserAuthStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MelonModule {

    @Provides
    fun provideControllerConfigurationFactory(): ControllerConfigurationFactory {
        return DefaultControllerConfigurationFactory()
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context, sharedPreferences: SharedPreferences, controllerConfigurationFactory: ControllerConfigurationFactory, json: Json, uriHandler: UriHandler): SettingsRepository {
        return SharedPreferencesSettingsRepository(context, sharedPreferences, controllerConfigurationFactory, json, uriHandler, CoroutineScope(Dispatchers.IO))
    }

    @Provides
    @Singleton
    fun provideSettingsBackupManager(@ApplicationContext context: Context, sharedPreferences: SharedPreferences): SettingsBackupManager {
        return SettingsBackupManager(context, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRomsRepository(@ApplicationContext context: Context, gson: Gson, settingsRepository: SettingsRepository, romFileProcessorFactory: RomFileProcessorFactory): RomsRepository {
        return FileSystemRomsRepository(context, gson, settingsRepository, romFileProcessorFactory)
    }

    @Provides
    @Singleton
    fun provideCheatsRepository(@ApplicationContext context: Context, database: MelonDatabase): CheatsRepository {
        return RoomCheatsRepository(context, database)
    }

    @Provides
    @Singleton
    fun provideNdsRomCache(@ApplicationContext context: Context, settingsRepository: SettingsRepository): NdsRomCache {
        return NdsRomCache(context, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideLayoutsRepository(@ApplicationContext context: Context, gson: Gson): LayoutsRepository {
        return InternalLayoutsRepository(context, gson)
    }

    @Provides
    @Singleton
    fun provideBackgroundsRepository(@ApplicationContext context: Context, gson: Gson): BackgroundRepository {
        return InternalBackgroundsRepository(context, gson)
    }

    @Provides
    @Singleton
    fun provideSaveStatesRepository(settingsRepository: SettingsRepository, saveStateScreenshotProvider: SaveStateScreenshotProvider, uriHandler: UriHandler): SaveStatesRepository {
        return FileSystemSaveStatesRepository(settingsRepository, saveStateScreenshotProvider, uriHandler)
    }

    @Provides
    @Singleton
    fun provideDSiWareMetadataRepository(): DSiWareMetadataRepository {
        return NusDSiWareMetadataRepository()
    }

    @Provides
    @Singleton
    fun provideRetroAchievementsRepository(
        raApi: RAApi,
        retroAchievementsDao: RetroAchievementsDao,
        raUserAuthStore: RAUserAuthStore,
        sharedPreferences: SharedPreferences,
        @ApplicationContext context: Context,
    ): RetroAchievementsRepository {
        return AndroidRetroAchievementsRepository(raApi, retroAchievementsDao, raUserAuthStore, sharedPreferences, context)
    }

    @Provides
    @Singleton
    fun provideConfigurationDirectoryVerifier(@ApplicationContext context: Context, settingsRepository: SettingsRepository): ConfigurationDirectoryVerifier {
        return FileSystemConfigurationDirectoryVerifier(context, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideFileRomProcessorFactory(@ApplicationContext context: Context, uriHandler: UriHandler, ndsRomCache: NdsRomCache): RomFileProcessorFactory {
        return Api24RomFileProcessorFactory(context, uriHandler, ndsRomCache)
    }

    @Provides
    @Singleton
    fun provideRomIconProvider(@ApplicationContext context: Context, romFileProcessorFactory: RomFileProcessorFactory): RomIconProvider {
        return RomIconProvider(context, romFileProcessorFactory)
    }

    @Provides
    @Singleton
    fun provideSaveStateScreenshotProvider(@ApplicationContext context: Context, picasso: Picasso): SaveStateScreenshotProvider {
        return SaveStateScreenshotProvider(context, picasso)
    }

    @Provides
    @Singleton
    fun provideBackgroundThumbnailProvider(@ApplicationContext context: Context): BackgroundThumbnailProvider {
        return BackgroundThumbnailProvider(context)
    }

    @Provides
    @Singleton
    fun provideTouchVibrator(@ApplicationContext context: Context, settingsRepository: SettingsRepository): TouchVibrator {
        val vibrator = context.getSystemService<Vibrator>()!!
        val delegate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Api26VibratorDelegate(vibrator)
        } else {
            OldVibratorDelegate(vibrator)
        }
        return TouchVibrator(delegate, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideScreenUnitsConverter(@ApplicationContext context: Context): ScreenUnitsConverter {
        return ScreenUnitsConverter(context)
    }

    @Provides
    @Singleton
    fun provideDeviceLayoutDisplayMapper(@ApplicationContext context: Context): DeviceLayoutDisplayMapper {
        return if (Build.MANUFACTURER == "AYN" && Build.MODEL == "AYN Thor") {
            AynThorLayoutDisplayMapper(context)
        } else {
            DefaultLayoutDisplayMapper(context)
        }
    }

    @Provides
    @Singleton
    fun provideDefaultLayoutBuilder(screenUnitsConverter: ScreenUnitsConverter): DefaultLayoutProvider {
        return DefaultLayoutProvider(screenUnitsConverter)
    }

    @Provides
    fun provideUILayoutProvider(defaultLayoutProvider: DefaultLayoutProvider): UILayoutProvider {
        return UILayoutProvider(defaultLayoutProvider)
    }

    @Provides
    @Singleton
    fun provideDSiNandManager(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        dSiWareMetadataRepository: DSiWareMetadataRepository,
        configurationDirectoryVerifier: ConfigurationDirectoryVerifier
    ): DSiNandManager {
        return AndroidDSiNandManager(context, settingsRepository, dSiWareMetadataRepository, configurationDirectoryVerifier)
    }

    @Provides
    @Singleton
    fun provideRomDetailsUiMapper(@ApplicationContext context: Context, layoutsRepository: LayoutsRepository): RomDetailsUiMapper {
        return RomDetailsUiMapper(context, layoutsRepository)
    }

    @Provides
    fun provideRomLaunchPreconditionChecker(
        configurationDirectoryVerifier: ConfigurationDirectoryVerifier,
        romFileProcessorFactory: RomFileProcessorFactory,
        dsiNandManager: DSiNandManager,
        settingsRepository: SettingsRepository,
    ): EmulatorLaunchPreconditionChecker {
        return EmulatorLaunchPreconditionChecker(
            configurationDirectoryVerifier = configurationDirectoryVerifier,
            romFileProcessorFactory = romFileProcessorFactory,
            dsiNandManager = dsiNandManager,
            settingsRepository = settingsRepository,
        )
    }
}