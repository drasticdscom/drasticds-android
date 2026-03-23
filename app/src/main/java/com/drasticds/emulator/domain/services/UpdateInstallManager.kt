package com.drasticds.emulator.domain.services

import kotlinx.coroutines.flow.Flow
import com.drasticds.emulator.domain.model.DownloadProgress
import com.drasticds.emulator.domain.model.appupdate.AppUpdate

interface UpdateInstallManager {
    fun downloadAndInstallUpdate(update: AppUpdate): Flow<DownloadProgress>
}