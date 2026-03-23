package com.drasticds.emulator.domain.model.appupdate

import android.net.Uri
import com.drasticds.emulator.domain.model.Version
import kotlin.time.Instant

data class AppUpdate(
    val type: Type,
    val id: Long,
    val downloadUri: Uri,
    val newVersion: Version,
    val description: String,
    val binarySize: Long,
    val updateDate: Instant,
) {

    enum class Type {
        PRODUCTION,
        NIGHTLY,
    }
}