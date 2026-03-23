package com.drasticds.emulator.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.drasticds.emulator.impl.BackgroundThumbnailProvider
import com.drasticds.emulator.impl.RomIconProvider
import com.drasticds.emulator.impl.image.CoilBackgroundThumbnailFetcher
import com.drasticds.emulator.impl.image.CoilRomIconFetcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    fun provideBackgroundThumbnailFetcher(backgroundThumbnailProvider: BackgroundThumbnailProvider): CoilBackgroundThumbnailFetcher.Factory {
        return CoilBackgroundThumbnailFetcher.Factory(backgroundThumbnailProvider)
    }

    @Provides
    fun provideRomIconFetcher(romIconProvider: RomIconProvider): CoilRomIconFetcher.Factory {
        return CoilRomIconFetcher.Factory(romIconProvider)
    }

    @Provides
    @Singleton
    fun provideCoilImageLoader(
        @ApplicationContext context: Context,
        coilBackgroundThumbnailFetcherFactory: CoilBackgroundThumbnailFetcher.Factory,
        coilRomIconFetcherFactory: CoilRomIconFetcher.Factory,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(coilBackgroundThumbnailFetcherFactory)
                add(coilRomIconFetcherFactory)
            }
            .crossfade(true)
            .build()
    }
}