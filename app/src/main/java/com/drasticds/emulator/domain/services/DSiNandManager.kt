package com.drasticds.emulator.domain.services

import android.net.Uri
import com.drasticds.emulator.domain.model.DSiWareTitle
import com.drasticds.emulator.domain.model.dsinand.ImportDSiWareTitleResult
import com.drasticds.emulator.domain.model.dsinand.OpenDSiNandResult
import com.drasticds.emulator.domain.model.dsinand.DSiWareTitleFileType

interface DSiNandManager {
    suspend fun openNand(): OpenDSiNandResult
    suspend fun listTitles(): List<DSiWareTitle>
    suspend fun importTitle(titleUri: Uri): ImportDSiWareTitleResult
    suspend fun deleteTitle(title: DSiWareTitle)
    suspend fun importTitleFile(title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri): Boolean
    suspend fun exportTitleFile(title: DSiWareTitle, fileType: DSiWareTitleFileType, fileUri: Uri): Boolean
    fun closeNand()
}