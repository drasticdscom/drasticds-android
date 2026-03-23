package com.drasticds.emulator.di.entrypoint

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.drasticds.emulator.initializer.AppForegroundStateTrackerInitializer
import com.drasticds.emulator.initializer.CoilInitializer

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {

    fun inject(coilInitializer: CoilInitializer)

    fun inject(appForegroundStateTrackerInitializer: AppForegroundStateTrackerInitializer)

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            val applicationContext = context.applicationContext ?: throw IllegalStateException()
            return EntryPointAccessors.fromApplication(
                context = applicationContext,
                entryPoint = InitializerEntryPoint::class.java
            )
        }
    }
}