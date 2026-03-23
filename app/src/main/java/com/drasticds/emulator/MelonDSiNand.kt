package com.drasticds.emulator

import com.drasticds.emulator.domain.model.DSiWareTitle
import com.drasticds.emulator.domain.model.EmulatorConfiguration

object MelonDSiNand {
    external fun openNand(emulatorConfiguration: EmulatorConfiguration): Int
    external fun listTitles(): ArrayList<DSiWareTitle>
    external fun importTitle(titleUri: String, tmdMetadata: ByteArray): Int
    external fun deleteTitle(titleId: Int)
    external fun importTitleFile(titleId: Int, fileType: Int, fileUri: String): Boolean
    external fun exportTitleFile(titleId: Int, fileType: Int, fileUri: String): Boolean
    external fun closeNand()
}