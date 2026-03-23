package com.drasticds.emulator.ui.cheats

import kotlinx.serialization.Serializable

sealed class CheatsNavigation {
    @Serializable
    data object Loading : CheatsNavigation()
    @Serializable
    data object GameList : CheatsNavigation()
    @Serializable
    data class GameFolders(val gameName: String?) : CheatsNavigation()
    @Serializable
    data class FolderCheats(val folderName: String?) : CheatsNavigation()
    @Serializable
    data object EnabledCheats : CheatsNavigation()
}