package com.drasticds.emulator.di

import android.content.Context
import android.os.Build
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.IntoMap
import com.drasticds.emulator.common.PermissionHandler
import com.drasticds.emulator.common.camera.BlackDSiCameraSource
import com.drasticds.emulator.common.camera.DSiCameraSource
import com.drasticds.emulator.common.romprocessors.RomFileProcessorFactory
import com.drasticds.emulator.common.runtime.ScreenshotFrameBufferProvider
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.domain.model.camera.DSiCameraSourceType
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.domain.services.EmulatorManager
import com.drasticds.emulator.impl.camera.DSiCameraSourceMultiplexer
import com.drasticds.emulator.impl.camera.PhysicalDSiCameraSource
import com.drasticds.emulator.impl.camera.StaticImageDSiCameraSource
import com.drasticds.emulator.impl.emulator.AndroidEmulatorManager
import com.drasticds.emulator.impl.emulator.EmulatorSession
import com.drasticds.emulator.impl.emulator.LifecycleOwnerProvider
import com.drasticds.emulator.impl.emulator.SramProvider
import com.drasticds.emulator.impl.image.BitmapFactoryBitmapLoader
import com.drasticds.emulator.impl.image.BitmapLoader
import com.drasticds.emulator.impl.image.ImageDecoderBitmapLoader


@Module
@InstallIn(ActivityRetainedComponent::class)
object EmulatorRuntimeModule {

    @MapKey
    private annotation class DSiCameraSourceKey(val value: DSiCameraSourceType)

    @Provides
    @ActivityRetainedScoped
    fun provideFrameBufferProvider(): ScreenshotFrameBufferProvider {
        return ScreenshotFrameBufferProvider()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideSramProvider(settingsRepository: SettingsRepository, uriHandler: UriHandler): SramProvider {
        return SramProvider(settingsRepository, uriHandler)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideEmulatorLifecycleOwnerProvider(): LifecycleOwnerProvider {
        return LifecycleOwnerProvider()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideBitmapLoader(@ApplicationContext context: Context): BitmapLoader {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoderBitmapLoader(context)
        } else {
            BitmapFactoryBitmapLoader(context)
        }
    }

    @Provides
    @IntoMap
    @DSiCameraSourceKey(DSiCameraSourceType.BLACK_SCREEN)
    fun provideBlackDSiCameraSource(): DSiCameraSource {
        return BlackDSiCameraSource()
    }

    @Provides
    @IntoMap
    @DSiCameraSourceKey(DSiCameraSourceType.PHYSICAL_CAMERAS)
    fun providePhysicalDSiCameraSource(
        @ApplicationContext context: Context,
        lifecycleOwnerProvider: LifecycleOwnerProvider,
        permissionHandler: PermissionHandler,
    ): DSiCameraSource {
        return PhysicalDSiCameraSource(context, lifecycleOwnerProvider, permissionHandler)
    }

    @Provides
    @IntoMap
    @DSiCameraSourceKey(DSiCameraSourceType.STATIC_IMAGE)
    fun provideStaticImageDSiCameraSource(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        bitmapLoader: BitmapLoader,
    ): DSiCameraSource {
        return StaticImageDSiCameraSource(context, settingsRepository, bitmapLoader)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideCameraManagerMultiplexer(
        settingsRepository: SettingsRepository,
        cameraSources: Map<DSiCameraSourceType, @JvmSuppressWildcards DSiCameraSource>,
    ): DSiCameraSourceMultiplexer {
        return DSiCameraSourceMultiplexer(
            dsiCameraSources = cameraSources,
            settingsRepository = settingsRepository,
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideEmulatorManager(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        sramProvider: SramProvider,
        screenshotFrameBufferProvider: ScreenshotFrameBufferProvider,
        romFileProcessorFactory: RomFileProcessorFactory,
        permissionHandler: PermissionHandler,
        cameraManagerMultiplexer: DSiCameraSourceMultiplexer,
    ): EmulatorManager {
        return AndroidEmulatorManager(
            context,
            settingsRepository,
            sramProvider,
            screenshotFrameBufferProvider,
            romFileProcessorFactory,
            permissionHandler,
            cameraManagerMultiplexer,
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideEmulatorSession(): EmulatorSession {
        return EmulatorSession()
    }
}