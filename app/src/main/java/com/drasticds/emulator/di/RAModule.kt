package com.drasticds.emulator.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import com.drasticds.emulator.common.network.MelonOkHttpInterceptor
import com.drasticds.emulator.common.retroachievements.AndroidRASignatureProvider
import com.drasticds.emulator.common.retroachievements.AndroidRAUserAuthStore
import com.drasticds.emulator.rcheevosapi.RASignatureProvider
import com.drasticds.emulator.rcheevosapi.RAApi
import com.drasticds.emulator.rcheevosapi.RAUserAuthStore
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RAModule {

    @Provides
    fun provideMelonOkHttpInterceptor(@ApplicationContext context: Context): MelonOkHttpInterceptor {
        return MelonOkHttpInterceptor(context)
    }

    @Provides
    @Named("ra-api-client")
    fun provideRAApiOkHttpClient(melonOkHttpInterceptor: MelonOkHttpInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(melonOkHttpInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRAUserAuthStore(sharedPreferences: SharedPreferences): RAUserAuthStore {
        return AndroidRAUserAuthStore(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRAAchievementSignatureProvider(): RASignatureProvider {
        return AndroidRASignatureProvider()
    }

    @Provides
    @Singleton
    fun provideRAApi(@Named("ra-api-client") client: OkHttpClient, json: Json, userAuthStore: RAUserAuthStore, achievementSignatureProvider: RASignatureProvider): RAApi {
        return RAApi(
            okHttpClient = client,
            json = json,
            userAuthStore = userAuthStore,
            signatureProvider = achievementSignatureProvider,
        )
    }
}