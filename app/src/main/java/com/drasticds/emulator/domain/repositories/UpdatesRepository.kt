package com.drasticds.emulator.domain.repositories

import com.drasticds.emulator.domain.model.appupdate.AppUpdate

interface UpdatesRepository {
    suspend fun checkNewUpdate(): Result<AppUpdate?>
    fun skipUpdate(update: AppUpdate)
    fun notifyUpdateDownloaded(update: AppUpdate)
}