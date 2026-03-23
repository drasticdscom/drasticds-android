package com.drasticds.emulator.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.drasticds.emulator.database.entities.CheatDatabaseEntity

@Dao
interface CheatDatabaseDao {
    @Insert
    suspend fun insertCheatDatabase(database: CheatDatabaseEntity): Long

    @Query("DELETE FROM cheat_database WHERE name = :databaseName")
    suspend fun deleteCheatDatabase(databaseName: String)
}