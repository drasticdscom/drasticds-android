package com.drasticds.emulator.domain.repositories

import android.net.Uri
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import com.drasticds.emulator.domain.model.Cheat
import com.drasticds.emulator.domain.model.CheatDatabase
import com.drasticds.emulator.domain.model.CheatFolder
import com.drasticds.emulator.domain.model.CheatImportProgress
import com.drasticds.emulator.domain.model.Game
import com.drasticds.emulator.domain.model.RomInfo
import com.drasticds.emulator.ui.cheats.model.CheatSubmissionForm

interface CheatsRepository {
    suspend fun getGames(): List<Game>
    suspend fun findGameForRom(romInfo: RomInfo): Game?
    fun getAllGameCheats(game: Game): Flow<List<CheatFolder>>
    fun getFolderCheats(folder: CheatFolder): Flow<List<Cheat>>
    suspend fun getRomEnabledCheats(romInfo: RomInfo): List<Cheat>
    suspend fun updateCheatsStatus(cheats: List<Cheat>)
    suspend fun addCheatFolder(folderName: String, game: Game)
    suspend fun deleteCheatDatabaseIfExists(databaseName: String)
    suspend fun addCheatDatabase(databaseName: String): CheatDatabase
    suspend fun addGameCheats(game: Game): Game
    suspend fun addCheat(folder: CheatFolder, cheat: Cheat)
    suspend fun addCustomCheat(folder: CheatFolder, cheatForm: CheatSubmissionForm)
    suspend fun updateCheat(cheat: Cheat)
    suspend fun deleteCheat(cheat: Cheat)
    fun importCheats(uri: Uri)
    fun isCheatImportOngoing(): Boolean
    fun getCheatImportProgress(): Observable<CheatImportProgress>
}