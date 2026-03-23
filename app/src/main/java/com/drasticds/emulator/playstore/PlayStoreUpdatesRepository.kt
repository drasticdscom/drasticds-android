package com.drasticds.emulator.playstore

import com.drasticds.emulator.domain.model.appupdate.AppUpdate
import com.drasticds.emulator.domain.repositories.UpdatesRepository

class PlayStoreUpdatesRepository : UpdatesRepository {
    override suspend fun checkNewUpdate(): Result<AppUpdate?> {
        return Result.success(null)
    }

    override fun skipUpdate(update: AppUpdate) {
        // Do nothing. Update checking not supported in the Play Store version
    }

    override fun notifyUpdateDownloaded(update: AppUpdate) {
        // Do nothing
    }
}