package com.drasticds.emulator.initializer

import android.content.Context
import androidx.startup.Initializer
import com.drasticds.emulator.di.entrypoint.InitializerEntryPoint
import com.drasticds.emulator.impl.system.AppForegroundStateTracker
import javax.inject.Inject

class AppForegroundStateTrackerInitializer : Initializer<Unit> {

    @Inject
    lateinit var appForegroundStateTracker: AppForegroundStateTracker

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(this)
        appForegroundStateTracker.startTracking(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}