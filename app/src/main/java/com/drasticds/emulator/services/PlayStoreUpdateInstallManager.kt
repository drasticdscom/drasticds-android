package com.drasticds.emulator.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.drasticds.emulator.domain.model.DownloadProgress
import com.drasticds.emulator.domain.model.appupdate.AppUpdate
import com.drasticds.emulator.domain.services.UpdateInstallManager

class PlayStoreUpdateInstallManager : UpdateInstallManager {
    override fun downloadAndInstallUpdate(update: AppUpdate): Flow<DownloadProgress> {
        return emptyFlow()
    }
}