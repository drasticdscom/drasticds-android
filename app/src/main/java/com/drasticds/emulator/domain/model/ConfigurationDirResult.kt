package com.drasticds.emulator.domain.model

data class ConfigurationDirResult(
        val consoleType: ConsoleType,
        val status: Status,
        val requiredFiles: Array<String>,
        val fileResults: Array<Pair<String, FileStatus>>
) {
    enum class Status {
        UNSET, INVALID, VALID
    }

    enum class FileStatus {
        PRESENT, MISSING, INVALID
    }
}